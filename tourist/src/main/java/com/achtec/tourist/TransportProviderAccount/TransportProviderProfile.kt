package com.achtec.tourist.TransportProviderAccount

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.achtec.tourist.databinding.ActivityTransportProviderProfileBinding

class TransportProviderProfile : AppCompatActivity() {
    private lateinit var binding: ActivityTransportProviderProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransportProviderProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}