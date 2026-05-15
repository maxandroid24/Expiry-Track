package com.expirytracker.data.mapper

import com.expirytracker.data.local.entity.ProductEntity
import com.expirytracker.domain.model.FreshnessStatus
import com.expirytracker.domain.model.Product
import java.util.*

fun ProductEntity.toDomainModel(): Product {
    val expiryDate = Date(this.expiryDate)
    val now = Date()
    val diff = expiryDate.time - now.time
    val daysLeft = diff / (1000 * 60 * 60 * 24)

    val status = when {
        expiryDate.before(now) -> FreshnessStatus.EXPIRED
        daysLeft <= 7 -> FreshnessStatus.EXPIRING_SOON
        else -> FreshnessStatus.FRESH
    }

    return Product(
        id = id,
        name = name,
        category = category,
        manufacturedDate = Date(manufacturedDate),
        expiryDate = expiryDate,
        notes = notes,
        imagePath = imagePath,
        freshnessStatus = status
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        category = category,
        manufacturedDate = manufacturedDate.time,
        expiryDate = expiryDate.time,
        notes = notes,
        imagePath = imagePath
    )
}
