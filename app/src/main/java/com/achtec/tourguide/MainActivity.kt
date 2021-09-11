package com.achtec.tourguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.achtec.tourguide.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appTitle.animation = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        binding.enterButton.animation = AnimationUtils.loadAnimation(this, R.anim.left_anim)
        binding.findBestGuide.animation = AnimationUtils.loadAnimation(this, R.anim.right_anim)
        binding.developer.animation = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)
        binding.enterButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}