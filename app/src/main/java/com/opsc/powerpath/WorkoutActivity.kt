package com.opsc.powerpath

import android.os.Bundle
import android.widget.ArrayAdapter
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.Data.Models.ExerciseProgress
import com.opsc.powerpath.Data.Models.Workout
import com.opsc.powerpath.Data.Models.WorkoutProgress

class WorkoutActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var chooseWorkoutButton: Button
    private lateinit var muscleGroupSpinner: Spinner
    private lateinit var exercisesRecyclerView: RecyclerView
    private lateinit var completeWorkoutButton: Button
    private lateinit var setsInput: EditText
    private val selectedExercises = mutableListOf<Exercise>()
    private val repsMap = mutableMapOf<Int, Int>() // Map to store reps for each exercise
    private lateinit var workoutActivityAdapter: WorkoutActivityAdapter
    private var selectedWorkout: Workout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)

        db = FirebaseFirestore.getInstance()
        chooseWorkoutButton = findViewById(R.id.chooseWorkoutButton)
        muscleGroupSpinner = findViewById(R.id.muscleGroupSpinner)
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView)
        completeWorkoutButton = findViewById(R.id.completeWorkoutButton)
        setsInput = findViewById(R.id.setsInput)

        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        workoutActivityAdapter = WorkoutActivityAdapter(selectedExercises) { position, reps ->
            repsMap[position] = reps // Store reps in the map
        }
        exercisesRecyclerView.adapter = workoutActivityAdapter

        val muscleGroups = arrayOf("legs", "push", "pull")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, muscleGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        muscleGroupSpinner.adapter = adapter

        chooseWorkoutButton.setOnClickListener { showWorkoutPopup() }
        completeWorkoutButton.setOnClickListener { completeWorkout() }
    }

    private fun showWorkoutPopup() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val muscleGroup = muscleGroupSpinner.selectedItem.toString()

        db.collection("users").document(userId)
            .collection("workouts")
            .whereEqualTo("muscleGroup", muscleGroup)
            .get()
            .addOnSuccessListener { documents ->
                val workouts = documents.map { document ->
                    document.toObject(Workout::class.java).apply { id = document.id }
                }
                if (workouts.isEmpty()) {
                    Toast.makeText(this, "No workouts found for the selected muscle group", Toast.LENGTH_SHORT).show()
                } else {
                    val workoutNames = workouts.map { it.name }.toTypedArray()
                    AlertDialog.Builder(this)
                        .setTitle("Select Workout")
                        .setItems(workoutNames) { _, which ->
                            selectedWorkout = workouts[which]
                            loadExercises(selectedWorkout!!)
                        }
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load workouts: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("WorkoutActivity", "Failed to load workouts", e)
            }
    }

    private fun loadExercises(workout: Workout) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val workoutId = workout.id
        if (workoutId == null) {
            Log.e("WorkoutActivity", "Workout ID is null")
            Toast.makeText(this, "Failed to load exercises: Workout ID is null", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId)
            .collection("workouts").document(workoutId)
            .collection("exercises")
            .get()
            .addOnSuccessListener { documents ->
                selectedExercises.clear()
                selectedExercises.addAll(documents.map { it.toObject(Exercise::class.java) })
                workoutActivityAdapter.notifyDataSetChanged()
                Log.d("WorkoutActivity", "Exercises loaded: ${selectedExercises.map { it.name }}")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load exercises: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("WorkoutActivity", "Failed to load exercises", e)
            }
    }

    private fun completeWorkout() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val workoutId = selectedWorkout?.id ?: return
        val sets = setsInput.text.toString().toIntOrNull() ?: 0

        val workoutProgress = WorkoutProgress(sets)
        db.collection("users").document(userId)
            .collection("workouts").document(workoutId)
            .collection("progress")
            .add(workoutProgress)
            .addOnSuccessListener {
                selectedExercises.forEachIndexed { index, exercise ->
                    val reps = repsMap[index] ?: 0 // Get reps from the map
                    val exerciseProgress = ExerciseProgress(reps, sets, exercise.name ?: "")
                    db.collection("users").document(userId)
                        .collection("workouts").document(workoutId)
                        .collection("progress").document(it.id)
                        .collection("exercises")
                        .add(exerciseProgress)
                }
                Toast.makeText(this, "Workout progress saved", Toast.LENGTH_SHORT).show()
                finish() // Navigate back to home screen
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save workout progress: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("WorkoutActivity", "Failed to save workout progress", e)
            }
    }
}