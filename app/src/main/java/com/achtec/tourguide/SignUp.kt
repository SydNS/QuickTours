package com.achtec.tourguide

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.achtec.tourguide.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ArrayAdapter.createFromResource(
            this,
            R.array.sign_up_options,
            android.R.layout.simple_spinner_item
        )
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.signUpSpinner.adapter = adapter
            }
        binding.signUpSpinner.setSelection(0)
        binding.signUpSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var intent: Intent
                if (p2 == 0) {
                    return
                }
                if (p2 == 1) {
                    intent = Intent(this@SignUp, SignUpAsTourist::class.java)
                    startActivity(intent)
                }
                if (p2 == 2) {
                    intent = Intent(this@SignUp, SignUpAsTourGuide::class.java)
                    startActivity(intent)
                }
                if (p2 == 3) {
                    intent = Intent(this@SignUp, SignUpAsTransportProvider::class.java)
                    startActivity(intent)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }
        }

        binding.logInLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}