package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddExerciseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise_k)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        OnExit()
        toWorkout()

    }

    private fun OnExit() {
        val back = findViewById<ImageView>(R.id.imageView2)
        back.setOnClickListener {
            intent = Intent(this, ContainerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun toWorkout() {
        val toWorkout = findViewById<CardView>(R.id.addWorkoutCard)
        toWorkout.setOnClickListener {
            intent = Intent(this, AddWorkoutActivity::class.java)
            startActivity(intent)
        }
    }

}