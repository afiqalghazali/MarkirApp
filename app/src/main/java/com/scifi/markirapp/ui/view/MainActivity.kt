package com.scifi.markirapp.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.scifi.markirapp.R
import com.scifi.markirapp.databinding.ActivityMainBinding
import com.scifi.markirapp.utils.AppsUtils
import com.scifi.markirapp.utils.FirebaseAuthUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuthUtils.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            if (!AppsUtils.isNetworkAvailable(this)) {
                AppsUtils.showAlert(
                    this,
                    "No Internet Connection.",
                    isWarning = true,
                    primaryButtonText = "OK",
                    onPrimaryButtonClick = {
                        startActivity(Intent(this, WelcomeActivity::class.java))
                        finish()
                    }
                )
            } else {
                binding.bottomNavigation.selectedItemId = R.id.nav_maps

                val firebaseUser = auth.currentUser
                if (firebaseUser == null) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                    return
                }
            }
        }
    }

    private fun setupAction() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            FirebaseAuthUtils.sessionEndedAlert(this)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_maps -> {
                    replaceFragment(MapsFragment())
                    true
                }

                R.id.nav_park -> {
                    replaceFragment(ParkingFragment())
                    true
                }

                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }

}