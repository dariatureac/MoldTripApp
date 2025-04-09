package com.example.moldtripapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.moldtrippapp.R

class ChisinauActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chisinau)

        // Chisinau image and title
        val chisinauImage = findViewById<ImageView>(R.id.chisinauImage)
        chisinauImage.setImageResource(R.drawable.chisinau0)

        val chisinauText = findViewById<TextView>(R.id.chisinauText)
        chisinauText.text = getString(R.string.chisinau_text)

        // Parks image and text
        val parksImage = findViewById<ImageView>(R.id.parksImage)
        parksImage.setImageResource(R.drawable.chisinau_parks)

        val parksText = findViewById<TextView>(R.id.parksText)
        parksText.text = getString(R.string.parks_text)

        // Museums image and text
        val museumsImage = findViewById<ImageView>(R.id.museumsImage)
        museumsImage.setImageResource(R.drawable.chisinau_museums)

        val museumsText = findViewById<TextView>(R.id.museumsText)
        museumsText.text = getString(R.string.museums_text)

        // Monuments image and text
        val monumentsImage = findViewById<ImageView>(R.id.monumentsImage)
        monumentsImage.setImageResource(R.drawable.chisinau_monuments)

        val monumentsText = findViewById<TextView>(R.id.monumentsText)
        monumentsText.text = getString(R.string.monuments_text)

        // Churches and Monasteries image and text
        val churchesImage = findViewById<ImageView>(R.id.churchesImage)
        churchesImage.setImageResource(R.drawable.chisinau_churches_monasteries)

        val churchesText = findViewById<TextView>(R.id.churchesText)
        churchesText.text = getString(R.string.churches_text)
    }
}