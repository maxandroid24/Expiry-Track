package com.expirytracker.presentation

import com.expirytracker.domain.model.Product

data class HomeState(
    val products: List<Product> = emptyList(),
    val totalCount: Int = 0,
    val expiredCount: Int = 0,
    val expiringSoonCount: Int = 0,
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)
