package com.opsc.powerpath

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var spText: TextView
    private lateinit var spinner: Spinner
    private val selectedExercises = mutableListOf<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_exercise)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getListOfExercises()
        //setupSaveButton()
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
                        spinner.adapter = adapter
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

//    private fun setupSaveButton() {
//        val saveButton = findViewById<Button>(R.id.save_button)
//        saveButton.setOnClickListener {
//            val selectedExerciseName = spinner.selectedItem.toString()
//            val selectedExercise = Exercise(name = selectedExerciseName)
//            selectedExercises.add(selectedExercise)
//            saveWorkout()
//        }
//    }

//    private fun saveWorkout() {
//        val userId = FirebaseAuth.getInstance().currentUser!!.uid
//        val workout = Workout(exercises = selectedExercises)
//
//        val apiService = RetrofitInstance.api.addWorkout(userId, workout)
//        apiService.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "Workout saved successfully")
//                } else {
//                    Log.e(TAG, "Failed to save workout: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                Log.e(TAG, "API call failed: ${t.message}")
//            }
//        })
//    }
}