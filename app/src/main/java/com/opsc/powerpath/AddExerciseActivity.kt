package com.opsc.powerpath

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.material.appbar.MaterialToolbar
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
    private val muscleGroups = arrayOf("Legs", "Push", "Pull")
    private val CHANNEL_ID = "exercise_channel"

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
        val toolbar: MaterialToolbar = findViewById(R.id.top)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        createNotificationChannel()
    }

    private fun addExerciseToApi(name: String, muscleGroup: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val exercise = Exercise(id = "", muscleGroup = muscleGroup, name = name)
        val apiService = RetrofitInstance.api.addExercise(userId, exercise)

        apiService.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddExerciseActivity, "Exercise added successfully", Toast.LENGTH_SHORT).show()
                    showNotification("Exercise Added", "Your exercise has been added successfully.")
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

    private fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Exercise Notifications"
            val descriptionText = "Notifications for exercise updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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