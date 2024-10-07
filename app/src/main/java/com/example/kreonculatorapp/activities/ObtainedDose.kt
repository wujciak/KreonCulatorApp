package com.example.kreonculatorapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Data
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.notification.NotificationWorker

class ObtainedDose : AppCompatActivity() {
    private lateinit var remindButton: Button
    lateinit var notificationManager: NotificationManager
    private val channelId = "i.apps.notifications"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obtained_dose)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
        val result = intent.getIntExtra("result", 0)
        val resultTextView = findViewById<TextView>(R.id.result)
        resultTextView.text = result.toString()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Kreon Dose Channel"
            val descriptionText = "Channel for Kreon dose reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeViews() {
        remindButton = findViewById(R.id.remindButton)
    }

    private fun setupListeners() {
        remindButton.setOnClickListener {
            Log.d("ObtainedDose", "Remind button clicked")
            scheduleNotification()
        }
    }

    private fun scheduleNotification() {
        val notificationId = System.currentTimeMillis().toInt()

        val data = Data.Builder()
            .putInt(NotificationWorker.NOTIFICATION_ID, notificationId)
            .build()

        val delay = 5L // 5 minutes
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
