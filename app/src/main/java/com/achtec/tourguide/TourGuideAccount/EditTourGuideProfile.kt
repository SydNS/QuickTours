package com.achtec.tourguide.TourGuideAccount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivityEditTourGuideProfileBinding

class EditTourGuideProfile : AppCompatActivity() {
    private lateinit var binding: ActivityEditTourGuideProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTourGuideProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}