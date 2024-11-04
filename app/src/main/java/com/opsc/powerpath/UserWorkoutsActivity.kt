package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserWorkoutsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutListAdapter
    private lateinit var noWorkouts: CardView
    private val workouts = mutableListOf<Workout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_workouts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.top)
        setSupportActionBar(toolbar)

        // Handle navigation icon click
        toolbar.setNavigationOnClickListener {
            finish()
        }
        noWorkouts = findViewById(R.id.noWorkouts) // Ensure this is a CardView
        recyclerView = findViewById(R.id.rvW)
        recyclerView.layoutManager = LinearLayoutManager(this)
        workoutAdapter = WorkoutListAdapter(workouts)
        recyclerView.adapter = workoutAdapter
        fetchWorkouts()
    }

private fun fetchWorkouts() {
    val userId = "j6NZPIfoG0hpODVLeblN"
    //val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    userRef.collection("workouts")
        .get()
        .addOnSuccessListener { workoutDocuments ->
          //  workouts.clear() // Clear the list before adding new items
            for (workoutDocument in workoutDocuments) {
                val workout = workoutDocument.toObject(Workout::class.java)
                workout.id = workoutDocument.id // Use the document ID as the workout ID

                // Fetch exercises for each workout
                workoutDocument.reference.collection("exercises")
                    .get()
                    .addOnSuccessListener { exerciseDocuments ->
                        val exercises = mutableListOf<Exercise>()
                        for (exerciseDocument in exerciseDocuments) {
                            val exercise = exerciseDocument.toObject(Exercise::class.java)
                            exercises.add(exercise)
                        }
                        workout.exercises = exercises // Set exercises to the workout
                        workouts.add(workout) // Add workout to the list

                        // Notify adapter after all workouts and exercises are fetched
                        if (workouts.size == workoutDocuments.size()) {
                            workoutAdapter.notifyDataSetChanged()
                            if (workouts.isEmpty()) {
                                noWorkouts.visibility = View.VISIBLE
                                Toast.makeText(this, "No workouts found", Toast.LENGTH_SHORT).show()
                            } else {
                                noWorkouts.visibility = View.GONE
                                Toast.makeText(this, "Successfully fetched workouts", Toast.LENGTH_SHORT).show()
                            }
                            Log.d("UserWorkoutsActivity", "Successfully fetched workouts: ${workouts.size}")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("UserWorkoutsActivity", "Error fetching exercises", exception)
                        Toast.makeText(this, "Error fetching exercises", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e("UserWorkoutsActivity", "Error fetching workouts", exception)
            Toast.makeText(this, "Error fetching workouts", Toast.LENGTH_SHORT).show()
        }
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