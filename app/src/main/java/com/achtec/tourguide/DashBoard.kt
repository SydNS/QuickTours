package com.achtec.tourguide

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.achtec.tourguide.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView

class DashBoard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        supportActionBar?.title = "DashBoard"
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        binding.navView.bringToFront()
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)

        binding.findTourGuides.setOnClickListener {
            startActivity(Intent(this, TourGuide::class.java))
        }
        binding.findTransporters.setOnClickListener {
            startActivity(Intent(this, TransportProvider::class.java))
        }
        binding.eWallet.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else
            super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_tour_guides -> startActivity(Intent(this, TourGuide::class.java))
            R.id.nav_wallet -> startActivity(Intent(this, WalletActivity::class.java))
            R.id.nav_profile -> Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
            R.id.nav_logout -> Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show()
            R.id.nav_share -> Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
            R.id.nav_about -> Toast.makeText(this, "About the app", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}