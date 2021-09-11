package com.achtec.tourguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivitySignUpAsTourGuideBinding

class SignUpAsTourGuide : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpAsTourGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpAsTourGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tourGuideToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }
}