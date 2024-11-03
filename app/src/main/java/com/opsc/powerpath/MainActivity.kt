package com.opsc.powerpath

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var biometricPromptManager: BiometricPromptManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize BiometricPromptManager
        biometricPromptManager = BiometricPromptManager(this)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("com.opsc.powerpath", MODE_PRIVATE)

        // Check if it's the first time opening the app
        if (isFirstTimeOpeningApp()) {
            navigateToGetStartedPage()
        } else {
            // Check if the user is already signed in
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // User is signed in, check for biometric authentication
                if (biometricPromptManager.isBiometricAvailable()) {
                    biometricPromptManager.showBiometricPrompt(this as FragmentActivity, {
                        navigateToHomePage()
                    }, {
                        // Handle biometric authentication failure
                        Toast.makeText(this, "Biometric authentication failed", Toast.LENGTH_SHORT).show()
                    }, {
                        // Handle "Use account password" action
                        navigateToLoginPage()
                    })
                } else {
                    navigateToHomePage()
                }
            } else {
                // User is not signed in, navigate to the login page
                navigateToLoginPage()
            }
        }
    }

    private fun isFirstTimeOpeningApp(): Boolean {
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        }
        return isFirstTime
    }

    private fun navigateToGetStartedPage() {
        val intent = Intent(this, GetStartedActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, ContainerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}