package com.opsc.powerpath

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.Data.Models.Workout
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddWorkoutActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private val selectedExercises = mutableListOf<Exercise>()
    private var m_Text = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_workout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        muscleGroupCard()
        getListOfExercises()
        setupSaveButton()
    }

    private fun getListOfExercises() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val apiService = RetrofitInstance.api.getAllExercises(userId)
        apiService.enqueue(object : Callback<List<Exercise>> {
            override fun onResponse(call: Call<List<Exercise>>, response: Response<List<Exercise>>) {
                if (response.isSuccessful) {
                    val exercises = response.body()
                    exercises?.let {
                        val exerciseNames = it.map { exercise -> exercise.name }
                        val adapter = ArrayAdapter(this@AddWorkoutActivity, android.R.layout.simple_spinner_item, exerciseNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        //spinner.adapter = adapter
                    }
                } else {
                    Log.e(TAG, "Failed to get exercises: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Exercise>>, t: Throwable) {
                Log.e(TAG, "API call failed: ${t.message}")
            }
        })
    }

    private fun showMuscleGroupDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Title")

        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("OK"
        ) { dialog, which -> m_Text = input.text.toString() }
        builder.setNegativeButton("Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun muscleGroupCard() {
        val muscleGroup = findViewById<CardView>(R.id.muscle_group_card)
        muscleGroup.setOnClickListener {
            showMuscleGroupDialog()
        }
    }

    private fun setupSaveButton() {
        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {

            if (m_Text.isEmpty()) {
                Toast.makeText(this, "Please enter the muscle group first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //val selectedExerciseName = spinner.selectedItem.toString()
            val selectedExercise = Exercise(muscleGroup = m_Text, name = "test")
            selectedExercises.add(selectedExercise)
            saveWorkout()
        }
    }

    private fun saveWorkout() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val workout = Workout(exercises = selectedExercises)

        val apiService = RetrofitInstance.api.addWorkout(userId, workout)
        apiService.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Workout saved successfully")
                } else {
                    Log.e(TAG, "Failed to save workout: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "API call failed: ${t.message}")
            }
        })
    }
}