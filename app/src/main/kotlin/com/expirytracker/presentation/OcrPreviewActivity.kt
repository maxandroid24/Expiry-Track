package com.expirytracker.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.expirytracker.databinding.ActivityOcrPreviewBinding

class OcrPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOcrPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = intent.getStringExtra("RAW_TEXT") ?: ""
        binding.tvRawText.text = text

        binding.btnProcess.setOnClickListener {
            // Logic to return to AddProduct with processed data
            finish()
        }
    }
}
