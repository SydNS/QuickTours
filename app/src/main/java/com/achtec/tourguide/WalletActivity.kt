package com.achtec.tourguide

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.achtec.tourguide.databinding.ActivityWalletBinding

class WalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Wallet"
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refillBtn.setOnClickListener {
            val refillDialog = Dialog(this)
            refillDialog.setContentView(R.layout.top_up_dialog)
            refillDialog.setCancelable(true)
            refillDialog.show()
        }
    }
}