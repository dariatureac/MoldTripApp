package com.example.moldtripapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.moldtrippapp.R

class FirstScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)

        val textView: TextView = findViewById(R.id.moldTripText)
        textView.text = "MoldTrip"

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Finish so user can't go back to this screen
        }, 3000) // 3-second delay before moving to MainActivity
    }
}