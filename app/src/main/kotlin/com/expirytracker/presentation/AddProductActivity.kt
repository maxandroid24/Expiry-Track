package com.expirytracker.presentation

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.expirytracker.R
import com.expirytracker.databinding.ActivityAddProductBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()
    
    private var mfgDate: Date = Date()
    private var expDate: Date = Date()
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivProductPreview.setImageURI(it)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
            viewModel.onScanImage(bitmap)
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            binding.ivProductPreview.setImageBitmap(it)
            viewModel.onScanImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategoryDropdown()
        setupDatePickers()
        setupButtons()
        observeState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf("Food", "Medicine", "Cosmetics", "Grocery", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        binding.autoCompleteCategory.setAdapter(adapter)
    }

    private fun setupDatePickers() {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        binding.etMfgDate.setOnClickListener {
            showDatePicker { date ->
                mfgDate = date
                binding.etMfgDate.setText(sdf.format(date))
            }
        }
        
        binding.etExpDate.setOnClickListener {
            showDatePicker { date ->
                expDate = date
                binding.etExpDate.setText(sdf.format(date))
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupButtons() {
        binding.btnScan.setOnClickListener {
            // Show choice dialog
            pickImage.launch("image/*")
        }
        
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val category = binding.autoCompleteCategory.text.toString()
            val notes = binding.etNotes.text.toString()
            
            if (name.isBlank()) {
                binding.etName.error = "Name required"
                return@setOnClickListener
            }
            
            viewModel.saveProduct(name, category, mfgDate, expDate, notes, selectedImageUri?.toString())
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.loadingOverlay.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    
                    if (state.isSaved) {
                        Toast.makeText(this@AddProductActivity, "Product saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    
                    if (state.name.isNotEmpty()) {
                        binding.etName.setText(state.name)
                        binding.autoCompleteCategory.setText(state.category, false)
                        
                        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        state.mfgDate?.let { 
                            mfgDate = it
                            binding.etMfgDate.setText(sdf.format(it)) 
                        }
                        state.expDate?.let { 
                            expDate = it
                            binding.etExpDate.setText(sdf.format(it)) 
                        }
                        binding.etNotes.setText(state.notes)
                    }
                    
                    state.error?.let {
                        Toast.makeText(this@AddProductActivity, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
