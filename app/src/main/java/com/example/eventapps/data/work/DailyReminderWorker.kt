package com.example.eventapps.data.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eventapps.data.remote.retrofit.ApiConfig
import com.example.testingroomdatabase.R

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val response = ApiConfig.getApiService().getNearestActiveEvent(-1, 1).execute()
            if (response.isSuccessful) {
                val event = response.body()?.listEvents?.firstOrNull()
                if (event != null) {
                    showNotification(event.name, event.beginTime)
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "doWork: Error fetching event", e)
            Result.failure()
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("Recommendation event for you on $message")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        private const val TAG = "DailyReminderWorker"
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
        private const val NOTIFICATION_ID = 1
    }
}