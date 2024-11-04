package com.opsc.powerpath

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.opsc.powerpath.Data.API.IApi
import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.Data.API.ApiResponse
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddExerciseActivity : AppCompatActivity() {

    private lateinit var muscleGroupSpinner: Spinner
    private lateinit var exerciseNameEditText: EditText
    private lateinit var saveButton: Button
    private val muscleGroups = arrayOf("legs", "push", "pull")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise_k)

        muscleGroupSpinner = findViewById(R.id.muscleGroupSpinner)
        exerciseNameEditText = findViewById(R.id.exerciseNameEditText)
        saveButton = findViewById(R.id.saveButton)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, muscleGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        muscleGroupSpinner.adapter = adapter

        saveButton.setOnClickListener {
            val exerciseName = exerciseNameEditText.text.toString()
            val muscleGroup = muscleGroupSpinner.selectedItem.toString()
            addExerciseToApi(exerciseName, muscleGroup)
        }
    }

    private fun addExerciseToApi(name: String, muscleGroup: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val exercise = Exercise(id = "", muscleGroup = muscleGroup, name = name)
        val apiService = RetrofitInstance.api.addExercise(userId, exercise)

        apiService.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddExerciseActivity, "Exercise added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddExerciseActivity, "Failed to add exercise: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@AddExerciseActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}