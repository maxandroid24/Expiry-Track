package com.expirytracker.data.local.entity

import androidx.room.*
import java.util.Date

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val manufacturedDate: Long,
    val expiryDate: Long,
    val notes: String?,
    val imagePath: String?
)
