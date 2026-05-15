package com.expirytracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expirytracker.domain.model.Product
import com.expirytracker.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    fun loadProduct(id: Int) {
        viewModelScope.launch {
            _product.value = repository.getProductById(id)
        }
    }

    fun deleteProduct(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _product.value?.let { 
                repository.deleteProduct(it)
                onDeleted()
            }
        }
    }
}
