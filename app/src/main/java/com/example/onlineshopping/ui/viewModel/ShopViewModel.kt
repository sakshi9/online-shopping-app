package com.example.onlineshopping.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.domain.usecase.GetProductsUseCase
import com.example.onlineshopping.domain.usecase.GetSearchSuggestionsUseCase
import com.example.onlineshopping.ui.model.ShopUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getSuggestionsUseCase: GetSearchSuggestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    val suggestions: StateFlow<List<Product>> = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                flow {
                    getSuggestionsUseCase(query).onSuccess { emit(it) }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val products: Flow<PagingData<Product>> = _state
        .map { Triple(it.selectedCategory, it.searchQuery, it.sortBy) }
        .distinctUntilChanged()
        .flatMapLatest { (category, search, sort) ->
            getProductsUseCase(
                category = category,
                search = search,
                sort = sort
            )
        }
        .cachedIn(viewModelScope)

    fun setCategory(category: String) {
        _state.update {
            it.copy(
                selectedCategory = category,
                searchQuery = ""
            )
        }
        searchQuery.value = ""
    }

    fun setSort(sort: String) {
        _state.update { it.copy(sortBy = sort) }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        _state.update { it.copy(searchQuery = query) }
    }

    fun submitSearch() {
        searchQuery.value = ""
    }

    fun clearSuggestions() {
        searchQuery.value = ""
    }
}