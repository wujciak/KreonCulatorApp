package com.example.kreonculatorapp.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.notification.NotificationReceiver
import java.util.Calendar

class ObtainedDose : AppCompatActivity() {
    private lateinit var remindButton: Button
    lateinit var notificationManager: NotificationManager
    private val channelId = "i.apps.notifications"
    private val NOTIFICATION_ID = 0

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
        Log.d("ObtainedDose", "Scheduling notification")
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.NOTIFICATION_ID, NOTIFICATION_ID)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = Calendar.getInstance().timeInMillis + 2000 // 2 seconds

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                Log.d("ObtainedDose", "Exact alarm set for API level S+")
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            Log.d("ObtainedDose", "Exact alarm set for API level below S")
        }
    }
}
