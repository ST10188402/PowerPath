package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

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
        addExercise = findViewById(R.id.addDate)
        addDate = findViewById(R.id.addExercise)
     //   btnMenu = findViewById(R.id.btn_menu)
        bottomNavigationView = findViewById(R.id.nav_view)

        val toolbar: MaterialToolbar = findViewById(R.id.top)
        setSupportActionBar(toolbar)


        toolbar.setNavigationOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host)
            if (currentFragment is HomeFragment) {
                finish()
            } else if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host, HomeFragment())
                transaction.commit()
            }
        }
        // FAB BUTTON CLICK LISTENER
        newWorkout.setOnClickListener {
            onAddButtonClicked()
        }
        addExercise.setOnClickListener {
            val intent = Intent(this, AddExerciseActivity::class.java)
            startActivity(intent)
        }
        addDate.setOnClickListener {
            val intent = Intent(this, AddWorkoutActivity::class.java)
            startActivity(intent)
        }
//        btnMenu.setOnClickListener {
//            Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show()
//        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host)
            val transaction = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.home -> {
                    if (currentFragment !is HomeFragment) {
                        transaction.replace(R.id.nav_host, HomeFragment())
                        transaction.addToBackStack(null)
                        transaction.commit()
                        nh.text = getString(R.string.home)
                        toolbar.title = getString(R.string.home)
                    }
                    true
                }

                R.id.camera -> {
                    if (currentFragment !is TakePhotoFragment) {
                        transaction.replace(R.id.nav_host, TakePhotoFragment())
                        transaction.addToBackStack(null)
                        transaction.commit()
                        nh.text = getString(R.string.camera)
                        toolbar.title = getString(R.string.camera)
                    }
                    true
                }

                R.id.statistics -> {
                    if (currentFragment !is ProgressPage) {
                        transaction.replace(R.id.nav_host, ProgressPage())
                        transaction.addToBackStack(null)
                        transaction.commit()
                        nh.text = getString(R.string.statistic)
                        toolbar.title = getString(R.string.statistic)
                    }
                    true
                }

                R.id.nav_profile -> {
                    if (currentFragment !is ProfileFragment) {
                        transaction.replace(R.id.nav_host, ProfileFragment())
                        transaction.addToBackStack(null)
                        transaction.commit()
                        nh.text = getString(R.string.profile)
                        toolbar.title = getString(R.string.profile)
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                true
            }

            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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