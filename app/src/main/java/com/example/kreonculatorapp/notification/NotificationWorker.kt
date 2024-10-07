package com.example.kreonculatorapp.notification

import android.app.NotificationManager
import android.content.Context
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.activities.ObtainedDose

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val NOTIFICATION_ID = "notificationId"
    }

    override fun doWork(): Result {
        val notificationId = inputData.getInt(NOTIFICATION_ID, 0)

        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, ObtainedDose::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "i.apps.notifications")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Kreon Dose Reminder")
            .setContentText("Remember to take your Kreon dose!")
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)

        return Result.success()
    }
}