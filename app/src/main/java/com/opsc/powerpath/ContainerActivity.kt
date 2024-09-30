package com.opsc.powerpath

import android.os.Bundle
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
    private lateinit var addBtn : FloatingActionButton
    private lateinit var addExercise : FloatingActionButton
    private lateinit var addDate : FloatingActionButton

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private val clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        enableEdgeToEdge()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        addBtn = findViewById(R.id.fab)
        addExercise = findViewById(R.id.fab1)
        addDate = findViewById(R.id.fab2)
        bottomNavigationView = findViewById(R.id.nav_view)


        //FAB BUTTON CLICK LISTENER
        addBtn.setOnClickListener {
            onAddButtonClicked()
        }
        addExercise.setOnClickListener {
            Toast.makeText(this, "Add Exercise", Toast.LENGTH_SHORT).show()
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
                    }
                    true
                }
                R.id.statistics -> {
                    if (currentFragment !is TakePhotoFragment) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host, TakePhotoFragment())
                        transaction.commit()
                    }
                    true
                }
                R.id.camera -> {
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
    private fun onAddButtonClicked() {
        setVisibility(clicked = true)
        setAnimation(clicked = true)
        if(!clicked){
            setVisibility(clicked = false)
            setAnimation(clicked = false)
        }
    }
    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            addExercise.visibility = View.VISIBLE
            addDate.visibility = View.VISIBLE
        } else {
            addExercise.visibility = View.INVISIBLE
            addDate.visibility = View.INVISIBLE
        }
    }
    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            addExercise.startAnimation(fromBottom)
            addDate.startAnimation(fromBottom)
            addBtn.startAnimation(rotateOpen)
        } else {
            addExercise.startAnimation(toBottom)
            addDate.startAnimation(toBottom)
            addBtn.startAnimation(rotateClose)
        }
    }
}