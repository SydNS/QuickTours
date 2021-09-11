package com.achtec.tourguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivitySignUpAsTransportProviderBinding

class SignUpAsTransportProvider : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpAsTransportProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpAsTransportProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.transportToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }
}