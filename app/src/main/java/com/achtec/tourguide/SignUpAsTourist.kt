package com.achtec.tourguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivitySignUpAsTouristBinding

class SignUpAsTourist : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpAsTouristBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpAsTouristBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.touristToSignUp.setOnClickListener {
            startActivity(Intent(Intent(this, SignUp::class.java)))
            finish()
        }
    }
}