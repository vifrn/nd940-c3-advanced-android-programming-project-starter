package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var downloadManager : DownloadManager
    private lateinit var notificationManager: NotificationManager

    private lateinit var notificationBuilder : NotificationCompat.Builder
    private lateinit var receiverPendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var repo = DownloadOptions.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID) {
                val query = DownloadManager.Query()
                query.setFilterById(downloadID)
                val cursor: Cursor = downloadManager.query(query)

                cursor.moveToFirst()
                val successful = when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> true
                    else -> false
                }
                createActionPendingIntent(successful)
                receiverPendingIntent
                action = NotificationCompat.Action(null, getString(R.string.notification_button), receiverPendingIntent)
                notificationBuilder.addAction(action)

                custom_button.buttonState = ButtonState.Completed

                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            }
        }
    }

    private fun download() {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        repo = when(radioGroup.checkedRadioButtonId) {
            R.id.glide_radio_button -> DownloadOptions.GLIDE
            R.id.loadapp_radio_button -> DownloadOptions.LOADAPP
            R.id.retrofit_radio_button -> DownloadOptions.RETROFIT
            else -> DownloadOptions.NONE
        }

        val URL = when(repo) {
            DownloadOptions.GLIDE -> GLIDE_URL
            DownloadOptions.LOADAPP -> LOADAPP_URL
            DownloadOptions.RETROFIT -> RETROFIT_URL
            else -> null
        }

        if(URL == null) {
            Toast.makeText(this,
                getString(R.string.no_option_selected_toast_message), Toast.LENGTH_LONG).show()
        } else {
            val request =
                DownloadManager.Request(Uri.parse(URL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            custom_button.buttonState = ButtonState.Loading
            createNotificationBuilder(repo)
        }
    }

    fun createActionPendingIntent(downloadStatus : Boolean) {
        val contentIntent = Intent(applicationContext, DetailReceiver::class.java)
        contentIntent.putExtra("activity", createDetailIntent(downloadStatus))
        receiverPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            contentIntent,
            0
        )
    }

    fun createDetailIntent(downloadStatus : Boolean) : Intent {
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra("option", repo.ordinal)
        intent.putExtra("success", downloadStatus)
        return intent
    }

    private fun createNotificationBuilder (repo : DownloadOptions) {
        createNotificationChannel()

        val contentText = when (repo) {
            DownloadOptions.GLIDE -> getString(R.string.notification_glide_description)
            DownloadOptions.LOADAPP -> getString(R.string.notification_loadapp_description)
            DownloadOptions.RETROFIT -> getString(R.string.notification_retrofit_description)
            else -> ""
        }

        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_cloud_download)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.notification_title)))
            .setAutoCancel(true)
    }

    private fun createNotificationChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Your download is complete!"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    enum class DownloadOptions {NONE, GLIDE, LOADAPP, RETROFIT}

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"

        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "Notifications to inform you when your download is done"
        private const val NOTIFICATION_ID = 1
    }

}
