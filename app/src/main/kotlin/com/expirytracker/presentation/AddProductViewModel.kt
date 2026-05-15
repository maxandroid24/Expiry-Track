package com.expirytracker.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expirytracker.data.remote.AiExtractionService
import com.expirytracker.domain.model.Product
import com.expirytracker.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val aiService: AiExtractionService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductState())
    val uiState: StateFlow<AddProductState> = _uiState.asStateFlow()

    fun onScanImage(bitmap: Bitmap) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val extractedProduct = aiService.extractProductInfoFromImage(bitmap)
            if (extractedProduct != null) {
                _uiState.update { 
                    it.copy(
                        name = extractedProduct.name,
                        category = extractedProduct.category,
                        mfgDate = extractedProduct.manufacturedDate,
                        expDate = extractedProduct.expiryDate,
                        notes = extractedProduct.notes ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to extract info") }
            }
        }
    }

    fun saveProduct(name: String, category: String, mfgDate: Date, expDate: Date, notes: String, imagePath: String?) {
        viewModelScope.launch {
            val product = Product(
                name = name,
                category = category,
                manufacturedDate = mfgDate,
                expiryDate = expDate,
                notes = notes,
                imagePath = imagePath
            )
            repository.insertProduct(product)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}

data class AddProductState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val name: String = "",
    val category: String = "",
    val mfgDate: Date? = null,
    val expDate: Date? = null,
    val notes: String = "",
    val error: String? = null
)
