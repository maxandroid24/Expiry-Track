package com.expirytracker.data.remote

import android.graphics.Bitmap
import com.expirytracker.domain.model.Product
import java.util.Date

interface AiExtractionService {
    suspend fun extractProductInfoFromImage(image: Bitmap): Product?
    suspend fun extractProductInfoFromText(text: String): Product?
}
