package com.example.onlineshopping.ui.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopping.domain.usecase.GetProductsUseCase
import com.example.onlineshopping.domain.usecase.GetSearchSuggestionsUseCase
import com.example.onlineshopping.domain.usecase.LoadMoreProductsUseCase
import com.example.onlineshopping.ui.model.ShopUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val loadMoreProductsUseCase: LoadMoreProductsUseCase,
    private val getSuggestionsUseCase: GetSearchSuggestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        observeSearchSuggestions()
        loadProducts()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchSuggestions() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(emptyList())
                    } else {
                        flow {
                            val result = getSuggestionsUseCase(query)
                            emit(result.getOrDefault(emptyList()))
                        }
                    }
                }
                .collect { suggestions ->
                    _state.update { it.copy(suggestions = suggestions) }
                }
        }
    }

    fun setSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchQuery.value = query
    }

    fun submitSearch() {
        _state.update {
            it.copy(
                currentPage = 1,
                products = emptyList(),
                suggestions = emptyList()
            )
        }
        loadProducts()
    }

    fun clearSuggestions() {
        _state.update { it.copy(suggestions = emptyList()) }
    }

    fun setCategory(category: String) {
        _state.update {
            it.copy(
                selectedCategory = category,
                currentPage = 1,
                products = emptyList()
            )
        }
        loadProducts()
    }

    fun setSort(sort: String) {
        _state.update {
            it.copy(
                sortBy = sort,
                currentPage = 1,
                products = emptyList()
            )
        }
        loadProducts()
    }

    fun loadProducts() {
        val s = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = getProductsUseCase(
                s.selectedCategory,
                s.searchQuery,
                s.sortBy,
                1
            )

            result.onSuccess { response ->
                _state.update {
                    it.copy(
                        products = response.items,
                        total = response.total,
                        currentPage = 1,
                        totalPages = response.totalPages,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }

    fun loadMore() {
        val s = _state.value

        if (s.isLoadingMore || s.currentPage >= s.totalPages) return

        _state.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            val result = getProductsUseCase(
                category = s.selectedCategory,
                search = s.searchQuery,
                sort = s.sortBy,
                page = s.currentPage + 1
            )

            result.onSuccess { response ->
                _state.update {
                    it.copy(
                        products = it.products + response.items,
                        currentPage = response.page,
                        totalPages = response.totalPages,
                        isLoadingMore = false
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoadingMore = false) }
            }
        }
    }
}