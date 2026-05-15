package com.expirytracker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.expirytracker.R
import com.expirytracker.databinding.ItemProductBinding
import com.expirytracker.domain.model.FreshnessStatus
import com.expirytracker.domain.model.Product
import java.text.SimpleDateFormat
import java.util.*

class ProductAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvCategoryLabel.text = product.category
            
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val expiryStr = sdf.format(product.expiryDate)
            
            val now = Date()
            val diff = product.expiryDate.time - now.time
            val daysLeft = diff / (1000 * 60 * 60 * 24)
            
            binding.tvExpiryInfo.text = when {
                product.freshnessStatus == FreshnessStatus.EXPIRED -> "Expired on $expiryStr"
                daysLeft == 0L -> "Expires today!"
                daysLeft == 1L -> "Expires tomorrow"
                else -> "Expires in $daysLeft days ($expiryStr)"
            }

            val (badgeText, badgeBg, badgeTextColor) = when (product.freshnessStatus) {
                FreshnessStatus.EXPIRED -> Triple("EXPIRED", R.drawable.badge_background_expired, R.color.expired_red)
                FreshnessStatus.EXPIRING_SOON -> Triple("EXPIRING SOON", R.drawable.badge_background_expiring, R.color.expiring_soon_yellow)
                FreshnessStatus.FRESH -> Triple("FRESH", R.drawable.badge_background_fresh, R.color.fresh_green)
            }
            
            binding.tvStatusBadge.text = badgeText
            binding.tvStatusBadge.setBackgroundResource(badgeBg)
            binding.tvStatusBadge.setTextColor(ContextCompat.getColor(binding.root.context, badgeTextColor))

            if (product.imagePath != null) {
                binding.ivProduct.load(product.imagePath)
            } else {
                binding.ivProduct.setImageResource(R.mipmap.ic_launcher)
            }

            binding.root.setOnClickListener { onProductClick(product) }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
    }
}
