package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContainerActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var newWorkout : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      // setContentView(R.layout.activity_container)
        setContentView(R.layout.activity_home)
       // setContentView(R.layout.fragment_home)
        enableEdgeToEdge()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        newWorkout = findViewById(R.id.fab)
        bottomNavigationView = findViewById(R.id.nav_view)


        newWorkout.setOnClickListener {
          val intent = Intent(this, AddWorkoutActivity::class.java)
            startActivity(intent)
        }


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host)
            when (item.itemId) {
                R.id.home -> {
                    if (currentFragment !is HomeFragment) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, HomeFragment())
                        transaction.commit()
                    }
                    true
                }
                R.id.camera -> {
                    if (currentFragment !is TakePhotoFragment) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, TakePhotoFragment())
                        transaction.commit()
                    }
                    true
                }
                R.id.statistics -> {
                    if (currentFragment !is ProgressPage) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, ProgressPage())
                        transaction.commit()
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (currentFragment !is ProfileFragment) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, ProfileFragment())
                        transaction.commit()
                    }
                    true
                }
                else -> false
            }
        }

        /// Display ProfileFragment by default
       if (savedInstanceState == null) {
           val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host, HomeFragment())
            transaction.commit()
        }
    }
}