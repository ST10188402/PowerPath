package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
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
    private lateinit var addExerciseButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var muscleGroupSpinner: Spinner
    private val selectedExercises = mutableListOf<Exercise>()
    private lateinit var exerciseAdapter: ExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_workout)

        db = FirebaseFirestore.getInstance()
        workoutRecyclerView = findViewById(R.id.workoutRecyclerView)
        addWorkoutButton = findViewById(R.id.addWorkoutButton)
        addExerciseButton = findViewById(R.id.addExerciseButton)
        nameEditText = findViewById(R.id.workoutNameEditText)
        muscleGroupSpinner = findViewById(R.id.muscleGroupSpinner)
        val muscleGroups = arrayOf("Legs", "Push", "Pull")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, muscleGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        muscleGroupSpinner.adapter = adapter

        exerciseAdapter = ExerciseAdapter(selectedExercises)
        workoutRecyclerView.layoutManager = LinearLayoutManager(this)
        workoutRecyclerView.adapter = exerciseAdapter

        addExerciseButton.setOnClickListener { showExercisePopup() }
        addWorkoutButton.setOnClickListener { addWorkout() }
        val toolbar: MaterialToolbar = findViewById(R.id.top)
        setSupportActionBar(toolbar)


        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showExercisePopup() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val muscleGroup = muscleGroupSpinner.selectedItem.toString()

        db.collection("users").document(userId)
            .collection("exercises")
            .whereEqualTo("muscleGroup", muscleGroup)
            .get()
            .addOnSuccessListener { documents ->
                val exercises = documents.map { it.toObject(Exercise::class.java) }
                if (exercises.isEmpty()) {
                    Toast.makeText(this, "No exercises found for the selected muscle group", Toast.LENGTH_SHORT).show()
                } else {
                    val exerciseNames = exercises.map { it.name }.toTypedArray()
                    val selectedItems = BooleanArray(exercises.size)

                    AlertDialog.Builder(this)
                        .setTitle("Select Exercises")
                        .setMultiChoiceItems(exerciseNames, selectedItems) { _, which, isChecked ->
                            if (isChecked) {
                                selectedExercises.add(exercises[which])
                            } else {
                                selectedExercises.remove(exercises[which])
                            }
                        }
                        .setPositiveButton("OK") { _, _ ->
                            exerciseAdapter.notifyDataSetChanged()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load exercises: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddWorkoutActivity", "Failed to load exercises", e)
            }
    }

    private fun addWorkout() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val name = nameEditText.text.toString()

        if (name.isBlank()) {
            Toast.makeText(this, "Please enter a workout name", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedExercises.isEmpty()) {
            Toast.makeText(this, "Please select at least one exercise", Toast.LENGTH_SHORT).show()
            return
        }

        val muscleGroup = muscleGroupSpinner.selectedItem.toString()
        val workout = Workout(name = name, muscleGroup = muscleGroup, exercises = selectedExercises)
        val apiService = RetrofitInstance.api.addWorkout(userId, workout)

        apiService.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddWorkoutActivity, "Workout added successfully", Toast.LENGTH_SHORT).show()
                    finish()
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
}