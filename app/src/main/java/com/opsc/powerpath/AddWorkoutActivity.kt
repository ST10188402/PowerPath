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
    private lateinit var selectedExercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_workout)

        selectedExercise = intent.getParcelableExtra("selectedExercise") ?: run {
            Toast.makeText(this, "No exercise selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = FirebaseFirestore.getInstance()
        workoutRecyclerView = findViewById(R.id.workoutRecyclerView)
        addWorkoutButton = findViewById(R.id.addWorkoutButton)
        nameEditText = findViewById(R.id.workoutNameEditText)
        setsEditText = findViewById(R.id.setsEditText)
        repsEditText = findViewById(R.id.repsEditText)

        loadWorkoutsForExercise()
        addWorkoutButton.setOnClickListener { addWorkout() }
    }

    private fun loadWorkoutsForExercise() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val exerciseId = "u2LKjyLsmSXDO39GflvR"
        val apiService = RetrofitInstance.api.getWorkouts(userId, exerciseId)

        apiService.enqueue(object : Callback<List<Workout>> {
            override fun onResponse(call: Call<List<Workout>>, response: Response<List<Workout>>) {
                if (response.isSuccessful) {
                    val workouts = response.body() ?: emptyList()
                    if (workouts.isEmpty()) {
                        Toast.makeText(this@AddWorkoutActivity, "No workouts have been added", Toast.LENGTH_SHORT).show()
                    } else {
                        workoutRecyclerView.adapter = WorkoutAdapter(workouts)
                    }
                } else {
                    Toast.makeText(this@AddWorkoutActivity, "Failed to load workouts: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Workout>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun addWorkout() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val exerciseId = selectedExercise.id
        val name = nameEditText.text.toString()
        val sets = setsEditText.text.toString().toInt()
        val reps = repsEditText.text.toString().toInt()
        val workout = Workout(name = name, sets = sets, reps = reps)
        val apiService = RetrofitInstance.api.addWorkout(userId, exerciseId, workout)

        apiService.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    loadWorkoutsForExercise()
                } else {
                    Toast.makeText(this@AddWorkoutActivity, "Failed to add workout: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("AddWorkoutActivity", "Failed to add workout: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}