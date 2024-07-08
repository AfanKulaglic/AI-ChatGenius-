package com.example.chatgenius

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val checkButton = findViewById<Button>(R.id.button_get_started)

        checkButton.setOnClickListener {
            redirect()
        }
    }

    private fun redirect() {
        val intent = Intent(this, OnboadingActivity::class.java)
        startActivity(intent)
    }
}