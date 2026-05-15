package com.expirytracker.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.expirytracker.domain.model.Product
import com.expirytracker.domain.repository.ProductRepository
import com.expirytracker.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.*

@HiltWorker
class ExpiryReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ProductRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        val products = repository.getAllProducts().first()
        
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val expiringSoonProducts = products.filter { product ->
            val pCal = Calendar.getInstance().apply {
                time = product.expiryDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            pCal.time == tomorrow
        }

        expiringSoonProducts.forEach { product ->
            notificationHelper.showNotification(
                "Expiry Reminder",
                "${product.name} expires tomorrow."
            )
        }

        return Result.success()
    }
}
