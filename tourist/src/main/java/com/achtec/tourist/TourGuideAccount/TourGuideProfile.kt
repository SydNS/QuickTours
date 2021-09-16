package com.achtec.tourist.TourGuideAccount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourist.databinding.ActivityTourGuideProfileBinding

class TourGuideProfile : AppCompatActivity() {
    private lateinit var binding: ActivityTourGuideProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourGuideProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}