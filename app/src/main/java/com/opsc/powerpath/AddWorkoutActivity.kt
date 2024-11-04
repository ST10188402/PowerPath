package com.opsc.powerpath

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.opsc.powerpath.Data.API.ApiResponse
import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.Data.Models.Workout
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddWorkoutActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var workoutRecyclerView: RecyclerView
    private lateinit var addWorkoutButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var setsEditText: EditText
    private lateinit var repsEditText: EditText
    private lateinit var selectedExercise: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_workout)

        selectedExercise = "YC7zd4ASCxU9mepXcLli"

        db = FirebaseFirestore.getInstance()
        workoutRecyclerView = findViewById(R.id.workoutRecyclerView)
        addWorkoutButton = findViewById(R.id.addWorkoutButton)
        nameEditText = findViewById(R.id.workoutNameEditText)
        setsEditText = findViewById(R.id.setsEditText)
        repsEditText = findViewById(R.id.repsEditText)

        workoutRecyclerView.adapter = WorkoutAdapter(emptyList()) { workout, position ->
            handleWorkoutSelection(workout, position)
        }

        loadWorkoutsForExercise()
        addWorkoutButton.setOnClickListener { addWorkout() }
    }

    private fun loadWorkoutsForExercise() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val exerciseId = selectedExercise

        db.collection("users").document(userId).collection("exercises").document(exerciseId).collection("workouts")
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    val workouts = documents.toObjects(Workout::class.java)
                    if (workouts.isEmpty()) {
                        Toast.makeText(this@AddWorkoutActivity, "No workouts have been added", Toast.LENGTH_SHORT).show()
                    } else {
                        workoutRecyclerView.adapter = WorkoutAdapter(workouts) { workout, position ->
                            handleWorkoutSelection(workout, position)
                        }
                    }
                } else {
                    Toast.makeText(this@AddWorkoutActivity, "No workouts have been added", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@AddWorkoutActivity, "Failed to load workouts: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddWorkoutActivity", "Error getting workouts", exception)
            }
    }

    private fun addWorkout() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val exerciseId = selectedExercise
        val name = nameEditText.text.toString()
        val sets = setsEditText.text.toString().toInt()
        val reps = repsEditText.text.toString().toInt()
        val workout = Workout(id = "", name = name, sets = sets, reps = reps)
        val apiService = RetrofitInstance.api.addWorkout(userId, exerciseId, workout)

        apiService.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    finish()
                    Toast.makeText(this@AddWorkoutActivity, "Workout added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AddWorkoutActivity, "Failed to add workout: ${response.message()}", Toast.LENGTH_SHORT).show()
                    response.errorBody()?.string()?.let { errorMessage ->
                        println("Error: $errorMessage")
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun handleWorkoutSelection(workout: Workout, position: Int) {
        Toast.makeText(this, "Selected workout: ${workout.name} at position $position", Toast.LENGTH_SHORT).show()
    }

}