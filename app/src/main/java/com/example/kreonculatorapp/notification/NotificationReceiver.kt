package com.example.kreonculatorapp.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = "notificationId"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "onReceive called")
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        Log.d("NotificationReceiver", "Received broadcast with notificationId: $notificationId")

        val notification = NotificationCompat.Builder(context, "i.apps.notifications")
            .setContentTitle("Kreon Dose Reminder")
            .setContentText("Remember to take your Kreon dose!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
        Log.d("NotificationReceiver", "Notification posted")
    }
}
