package com.expirytracker.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.expirytracker.R
import com.expirytracker.databinding.ActivityProductDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private val viewModel: ProductDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra("PRODUCT_ID", -1)
        if (productId != -1) {
            viewModel.loadProduct(productId)
        }

        setupToolbar()
        setupButtons()
        observeState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupButtons() {
        binding.btnDelete.setOnClickListener {
            viewModel.deleteProduct { 
                finish()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.product.collectLatest { product ->
                    product?.let {
                        binding.tvDetailName.text = it.name
                        binding.chipCategory.text = it.category
                        binding.tvDetailStatus.text = it.freshnessStatus.name
                        
                        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                        binding.tvDetailExpiryDate.text = sdf.format(it.expiryDate)
                        binding.tvDetailNotes.text = it.notes ?: "No notes"
                        
                        if (it.imagePath != null) {
                            binding.ivProductImage.load(it.imagePath)
                        } else {
                            binding.ivProductImage.setImageResource(R.mipmap.ic_launcher)
                        }
                    }
                }
            }
        }
    }
}
