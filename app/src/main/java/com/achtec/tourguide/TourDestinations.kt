package com.achtec.tourguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivityTourDestinationsBinding

class TourDestinations : AppCompatActivity() {
    private lateinit var binding: ActivityTourDestinationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Tour Destinations"
        binding = ActivityTourDestinationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}