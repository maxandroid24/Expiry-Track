package com.expirytracker.data.remote

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.expirytracker.domain.model.Product
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import org.json.JSONObject

class GeminiExtractionServiceImpl @Inject constructor(
    private val apiKey: String
) : AiExtractionService {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    private val prompt = """
        Extract product information from this image. 
        Return a JSON object with:
        - name: Product name
        - category: Product category (Food, Medicine, Cosmetics, Grocery, Other)
        - manufacturedDate: Format YYYY-MM-DD
        - expiryDate: Format YYYY-MM-DD
        - notes: Any extra info
        If a field is not found, leave it null or "".
    """.trimIndent()

    override suspend fun extractProductInfoFromImage(image: Bitmap): Product? {
        return try {
            val response = model.generateContent(
                content {
                    image(image)
                    text(prompt)
                }
            )
            parseProductFromJson(response.text ?: "")
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun extractProductInfoFromText(text: String): Product? {
        return try {
            val response = model.generateContent(
                content {
                    text("Extract product info from this text: $text\n\n$prompt")
                }
            )
            parseProductFromJson(response.text ?: "")
        } catch (e: Exception) {
            null
        }
    }

    private fun parseProductFromJson(jsonString: String): Product? {
        return try {
            val cleanJson = jsonString.trim().removeSurrounding("```json", "```").trim()
            val json = JSONObject(cleanJson)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            
            val mfgDateStr = json.optString("manufacturedDate")
            val expDateStr = json.optString("expiryDate")
            
            Product(
                name = json.optString("name"),
                category = json.optString("category", "Other"),
                manufacturedDate = if (mfgDateStr.isNotEmpty()) sdf.parse(mfgDateStr) ?: Date() else Date(),
                expiryDate = if (expDateStr.isNotEmpty()) sdf.parse(expDateStr) ?: Date() else Date(),
                notes = json.optString("notes"),
                imagePath = null
            )
        } catch (e: Exception) {
            null
        }
    }
}
