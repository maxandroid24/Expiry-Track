package com.expirytracker.domain.model

import java.util.Date

data class Product(
    val id: Int = 0,
    val name: String,
    val category: String,
    val manufacturedDate: Date,
    val expiryDate: Date,
    val notes: String?,
    val imagePath: String?,
    val freshnessStatus: FreshnessStatus = FreshnessStatus.FRESH
)

enum class FreshnessStatus {
    FRESH,
    EXPIRING_SOON,
    EXPIRED
}
