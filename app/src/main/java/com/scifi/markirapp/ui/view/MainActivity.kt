package com.scifi.markirapp.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.scifi.markirapp.R
import com.scifi.markirapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_maps

            auth = Firebase.auth
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
                return
            }

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

                R.id.nav_Account -> {
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