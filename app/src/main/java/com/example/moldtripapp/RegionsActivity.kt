package com.example.moldtripapp

import com.example.moldtrippapp.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class RegionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regions)

        // Set images programmatically
        findViewById<ImageView>(R.id.image0).setImageResource(R.drawable.image0)
        findViewById<ImageView>(R.id.image1).setImageResource(R.drawable.image1)
        findViewById<ImageView>(R.id.image2).setImageResource(R.drawable.image2)
        findViewById<ImageView>(R.id.image3).setImageResource(R.drawable.image3)
        findViewById<ImageView>(R.id.image4).setImageResource(R.drawable.image4)
        findViewById<ImageView>(R.id.image5).setImageResource(R.drawable.image5)
        findViewById<ImageView>(R.id.image6).setImageResource(R.drawable.image6)
    }
}
