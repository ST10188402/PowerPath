package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.ExerciseAdapter
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExerciseActivity : AppCompatActivity() {

    private lateinit var muscleGroupSpinner: Spinner
    private lateinit var exerciseRecyclerView: RecyclerView
    private lateinit var addExerciseButton: Button
    private lateinit var addWorkoutButton: Button
    private val muscleGroups = arrayOf("Chest", "Back", "Legs", "Arms", "Shoulders", "Abs", "Full Body")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the muscleGroupSpinner before using it
        muscleGroupSpinner = findViewById(R.id.spinner_musclegroup)
        setupMuscleGroupSpinner()

        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView)
        addExerciseButton = findViewById(R.id.add_exercise)
        addWorkoutButton = findViewById(R.id.add_workout)

        OnExit()

        addExerciseButton.setOnClickListener {
            startActivity(Intent(this, AddExerciseActivity::class.java))
        }

        addWorkoutButton.setOnClickListener {
            startActivity(Intent(this, AddWorkoutActivity::class.java))
        }

        muscleGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                loadExercisesForSelectedMuscleGroup()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun OnExit() {
        val back = findViewById<ImageView>(R.id.imageView2)
        back.setOnClickListener {
            intent = Intent(this, ContainerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMuscleGroupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, muscleGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        muscleGroupSpinner.adapter = adapter
    }

    private fun loadExercisesForSelectedMuscleGroup() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val selectedGroup = muscleGroupSpinner.selectedItem.toString()

        val apiService = RetrofitInstance.api.getAllExercises(userId)
        apiService.enqueue(object : Callback<List<Exercise>> {
            override fun onResponse(call: Call<List<Exercise>>, response: Response<List<Exercise>>) {
                if (response.isSuccessful) {
                    val exercises = response.body()?.let { exerciseList ->
                        if (selectedGroup == "Full Body") {
                            exerciseList
                        } else {
                            exerciseList.filter { it.muscleGroup == selectedGroup }
                        }
                    } ?: emptyList()
                    exerciseRecyclerView.adapter = ExerciseAdapter(exercises)
                } else {
                    Toast.makeText(this@ExerciseActivity, "Failed to load exercises: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Exercise>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}