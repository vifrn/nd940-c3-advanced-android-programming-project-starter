package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val download = intent.getIntExtra("option", DownloadOptions.NONE.ordinal)
        val success = intent.getBooleanExtra("success", false)

        val statusText = findViewById<TextView>(R.id.status)
        when (success) {
            true -> {
                statusText.text = "Success"
                statusText.setTextColor(Color.GREEN)
            }
            else -> {
                statusText.text = "Fail"
                statusText.setTextColor(Color.RED)
            }
        }

        val fileNameText = findViewById<TextView>(R.id.file_name)
        fileNameText.text = when(download){
            DownloadOptions.GLIDE.ordinal -> getString(R.string.glide_button_description)
            DownloadOptions.LOADAPP.ordinal -> getString(R.string.loadapp_button_description)
            DownloadOptions.RETROFIT.ordinal -> getString(R.string.retrofit_button_description)
            else -> ""
        }

        findViewById<Button>(R.id.ok_button).setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

    }

}
