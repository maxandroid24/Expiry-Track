package com.expirytracker.data.repository

import com.expirytracker.data.local.dao.ProductDao
import com.expirytracker.data.mapper.toDomainModel
import com.expirytracker.data.mapper.toEntity
import com.expirytracker.domain.model.Product
import com.expirytracker.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> {
        return dao.getAllProducts().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getProductById(id: Int): Product? {
        return dao.getProductById(id)?.toDomainModel()
    }

    override suspend fun insertProduct(product: Product) {
        dao.insertProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: Product) {
        dao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        dao.deleteProduct(product.toEntity())
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        return dao.searchProducts(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun filterByCategory(category: String): Flow<List<Product>> {
        return dao.filterByCategory(category).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
