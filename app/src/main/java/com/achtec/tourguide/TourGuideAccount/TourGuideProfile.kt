package com.achtec.tourguide.TourGuideAccount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivityTourGuideProfileBinding

class TourGuideProfile : AppCompatActivity() {
    private lateinit var binding: ActivityTourGuideProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourGuideProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}