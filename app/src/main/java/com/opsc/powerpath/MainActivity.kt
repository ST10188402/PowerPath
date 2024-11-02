package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_get_started)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if the user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, navigate to the home page
            navigateToHomePage()
        } else {
            // User is not signed in, set up the "Get Started" button
            val btnGetStarted: Button = findViewById(R.id.btn_get_started)
            btnGetStarted.setOnClickListener {
                val intent = Intent(this, Onboarding::class.java)
                startActivity(intent)
            }
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, ContainerActivity::class.java)
        startActivity(intent)
        finish()
    }
}