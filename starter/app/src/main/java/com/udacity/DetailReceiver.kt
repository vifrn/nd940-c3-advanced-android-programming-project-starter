package com.udacity

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class DetailReceiver : BroadcastReceiver() {
    override fun onReceive(context : Context, intent : Intent) {
        val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        val newIntent = intent.extras?.get("activity") as Intent
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        context.startActivity(newIntent)
    }
}