package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.database

class SuccessActivity : AppCompatActivity() {
    private lateinit var btnHome: Button
    private lateinit var userName: TextView
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_success)


        val uid = CurrentUser.uid
        userName = findViewById(R.id.welcome_msg)
        btnHome = findViewById(R.id.btn_home)

        btnHome.setOnClickListener {
            val intent = Intent(this, ContainerActivity::class.java)
            startActivity(intent)
        }

        // Retrieve the user's name from the database
        if (uid != null) {
            database.reference.child("users").child(uid).get().addOnSuccessListener { dataSnapshot ->
                val firstName = dataSnapshot.child("name").value.toString()
                userName.text = "Welcome, $firstName"
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
