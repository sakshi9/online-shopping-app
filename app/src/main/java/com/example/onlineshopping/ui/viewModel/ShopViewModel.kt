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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getSuggestionsUseCase: GetSearchSuggestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: Flow<PagingData<Product>> = _state
        .map { Triple(it.selectedCategory, it.searchQuery, it.sortBy) }
        .distinctUntilChanged()         // skip emission if filters didn't actually change
        .flatMapLatest { (category, search, sort) ->
            getProductsUseCase(
                category = category,
                search = search,
                sort = sort
            )
        }
        .cachedIn(viewModelScope)

    private var searchJob: Job? = null

    fun setCategory(category: String) {
        _state.update {
            it.copy(
                selectedCategory = category,
                searchQuery = ""
            )
        }
    }

    fun setSort(sort: String) {
        _state.update {
            it.copy(sortBy = sort)
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update {
                it.copy(suggestions = emptyList())
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(250)
            getSuggestionsUseCase.invoke(query).onSuccess { suggestions ->
                _state.update {
                    it.copy(suggestions = suggestions)
                }
            }
        }
        searchQuery.value = query
    }

    fun submitSearch() {
        _state.update {
            it.copy(suggestions = emptyList())
        }
    }

    fun clearSuggestions() {
        _state.update { it.copy(suggestions = emptyList()) }
    }
}