package com.expirytracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.expirytracker.data.local.dao.ProductDao
import com.expirytracker.data.local.entity.ProductEntity

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {
    abstract val productDao: ProductDao

    companion object {
        const val DATABASE_NAME = "product_db"
    }
}
