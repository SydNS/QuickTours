package com.achtec.tourguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivityTransportProviderBinding

class TransportProvider : AppCompatActivity() {
    private lateinit var binding: ActivityTransportProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Find Transport Means"
        binding = ActivityTransportProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}