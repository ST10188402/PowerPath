package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.TextView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContainerActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var newWorkout: FloatingActionButton
    private lateinit var addExercise: FloatingActionButton
    private lateinit var addDate: FloatingActionButton

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        enableEdgeToEdge()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        val nh = findViewById<TextView>(R.id.nav_header)

        newWorkout = findViewById(R.id.fab)
        addExercise = findViewById(R.id.fab1)
        addDate = findViewById(R.id.fab2)
        bottomNavigationView = findViewById(R.id.nav_view)

        // FAB BUTTON CLICK LISTENER
        newWorkout.setOnClickListener {
            onAddButtonClicked()
        }
        addExercise.setOnClickListener {
            val intent = Intent(this, AddExerciseActivity::class.java)
            startActivity(intent)
        }
        addDate.setOnClickListener {
            Toast.makeText(this, "Add Date", Toast.LENGTH_SHORT).show()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host)
            when (item.itemId) {
                R.id.home -> {
                    if (currentFragment !is HomeFragment) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, HomeFragment())
                        transaction.commit()
                        nh.text = "Home"
                    }
                    true
                }

                R.id.camera -> {
                    if (currentFragment !is TakePhotoFragment) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, TakePhotoFragment())
                        transaction.commit()
                        nh.text = "Camera"
                    }
                    true
                }

                R.id.statistics -> {
                    if (currentFragment !is ProgressPage) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, ProgressPage())
                        transaction.commit()
                        nh.text = "Statistics"
                    }
                    true
                }

                R.id.nav_profile -> {
                    if (currentFragment !is ProfileFragment) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, ProfileFragment())
                        transaction.commit()
                        nh.text = "Profile"
                    }
                    true
                }

                else -> false
            }
        }

        // Display HomeFragment by default
        if (savedInstanceState == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host, HomeFragment())
            transaction.commit()
        }
    }

    private fun onAddButtonClicked() {
        clicked = !clicked
        setVisibility(clicked)
        setAnimation(clicked)
    }

    private fun setVisibility(clicked: Boolean) {
        if (clicked) {
            addExercise.visibility = View.VISIBLE
            addDate.visibility = View.VISIBLE
        } else {
            addExercise.visibility = View.INVISIBLE
            addDate.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (clicked) {
            addExercise.startAnimation(fromBottom)
            addDate.startAnimation(fromBottom)
            newWorkout.startAnimation(rotateOpen)
        } else {
            addExercise.startAnimation(toBottom)
            addDate.startAnimation(toBottom)
            newWorkout.startAnimation(rotateClose)
        }
    }
}