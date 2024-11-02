package com.opsc.powerpath

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.shape.MaterialShapeDrawable

class AddExerciseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise_k)

        val appBarLayout: AppBarLayout = findViewById(R.id.topAppBar)
        appBarLayout.statusBarForeground = MaterialShapeDrawable.createWithElevationOverlay(this)
        appBarLayout.setStatusBarForegroundColor(getColor(R.color.button_purple))

        val toolbar: MaterialToolbar = findViewById(R.id.top)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish() // Close the activity when the exit icon is clicked
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_nav, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                // Handle the menu item click here
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}