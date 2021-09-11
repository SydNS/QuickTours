package com.achtec.tourguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createAccountLink.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
        binding.logInBtn.setOnClickListener {
            startActivity(Intent(this, DashBoard::class.java))
            finish()
        }
    }
}