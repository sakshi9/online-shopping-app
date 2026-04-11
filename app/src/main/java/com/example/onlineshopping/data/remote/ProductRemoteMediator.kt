package com.example.onlineshopping.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.onlineshopping.data.local.AppDatabase
import com.example.onlineshopping.data.local.ProductDao
import com.example.onlineshopping.data.local.ProductEntity
import com.example.onlineshopping.data.local.RemoteKeysDao
import com.example.onlineshopping.data.local.RemoteKeysEntity
import com.example.onlineshopping.data.mapper.ToEntity

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(private val category: String,
                            private val search: String,
                            private val sort: String,
                            private val api: FakeGroceryApi,
                            private val productDao: ProductDao,
                            private val remoteKeysDao: RemoteKeysDao,
                            private val db: AppDatabase
): RemoteMediator<Int, ProductEntity>() {

    private val filterKey = "$category-$search-$sort"

    companion object {
        // Cache is considered fresh for 10 minutes
        private const val CACHE_TIMEOUT_MS = 10 * 60 * 1000L
    }

    override suspend fun initialize(): InitializeAction {
        val remoteKeys = remoteKeysDao.remoteKeysById(filterKey)
        val cacheAge   = System.currentTimeMillis() - (remoteKeys?.lastUpdated ?: 0L)
        return if (cacheAge < CACHE_TIMEOUT_MS) {
            InitializeAction.SKIP_INITIAL_REFRESH   // use cached data
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH // fetch fresh data
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {

            // 1. Resolve the page number to fetch
            val page: Int = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> null
                LoadType.APPEND  -> {
                    val remoteKeys = remoteKeysDao.remoteKeysById(filterKey)
                    remoteKeys?.nextPage
                }
            } ?: return MediatorResult.Success(endOfPaginationReached = true)

            // 2. Fetch from network
            val response = api.getProducts(
                category = category,
                search   = search,
                sort     = sort,
                page     = page,
                pageSize = state.config.pageSize
            )

            val products     = response.items
            val isLastPage   = response.page >= response.totalPages
            val nextPage     = if (isLastPage) null else page + 1
            val prevPage     = if (page == 1) null else page - 1

            // 3. Write to Room in a single transaction —
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    productDao.clearByFilter(category, search, sort)
                    remoteKeysDao.deleteById(filterKey)
                }

                // Save pagination bookmark
                remoteKeysDao.insertOrReplace(
                    RemoteKeysEntity(
                        id = filterKey,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                )

                // Map API models → Room entities and cache them
                val entities : List<ProductEntity> = products.map { product ->
                    product.ToEntity(
                        category = category,
                        search   = search,
                        sort     = sort,
                        page     = page
                    )
                }
                productDao.insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = isLastPage)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}