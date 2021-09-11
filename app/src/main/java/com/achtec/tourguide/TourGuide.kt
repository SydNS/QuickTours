package com.achtec.tourguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivityTourGuideBinding

class TourGuide : AppCompatActivity() {
    private lateinit var binding: ActivityTourGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Find Tour Guides"
        binding = ActivityTourGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tgSearchBtn.setOnClickListener {
            startActivity(Intent(this, TourDestinations::class.java))
        }
    }
}