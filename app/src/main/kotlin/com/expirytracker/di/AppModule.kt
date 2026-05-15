package com.expirytracker.di

import android.content.Context
import androidx.room.Room
import com.expirytracker.data.local.ProductDatabase
import com.expirytracker.data.local.dao.ProductDao
import com.expirytracker.data.repository.ProductRepositoryImpl
import com.expirytracker.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideProductDatabase(@ApplicationContext context: Context): ProductDatabase {
        return Room.databaseBuilder(
            context,
            ProductDatabase::class.java,
            ProductDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideProductDao(db: ProductDatabase): ProductDao {
        return db.productDao
    }

    @Provides
    @Singleton
    fun provideAiExtractionService(): com.expirytracker.data.remote.AiExtractionService {
        val apiKey = System.getenv("GEMINI_API_KEY") ?: ""
        return com.expirytracker.data.remote.GeminiExtractionServiceImpl(apiKey)
    }
}
