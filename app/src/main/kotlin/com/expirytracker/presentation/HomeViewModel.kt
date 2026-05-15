package com.expirytracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expirytracker.domain.model.FreshnessStatus
import com.expirytracker.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        _state.update { it.copy(isLoading = true) }
        repository.getAllProducts()
            .onEach { products ->
                val total = products.size
                val expired = products.count { it.freshnessStatus == FreshnessStatus.EXPIRED }
                val soon = products.count { it.freshnessStatus == FreshnessStatus.EXPIRING_SOON }
                
                _state.update { 
                    it.copy(
                        products = products,
                        totalCount = total,
                        expiredCount = expired,
                        expiringSoonCount = soon,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isEmpty()) {
            loadProducts()
        } else {
            repository.searchProducts(query)
                .onEach { products ->
                    _state.update { it.copy(products = products) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun deleteProduct(product: com.expirytracker.domain.model.Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
}
