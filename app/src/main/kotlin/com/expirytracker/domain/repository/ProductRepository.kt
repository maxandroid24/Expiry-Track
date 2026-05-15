package com.expirytracker.domain.repository

import com.expirytracker.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    fun searchProducts(query: String): Flow<List<Product>>
    fun filterByCategory(category: String): Flow<List<Product>>
}
