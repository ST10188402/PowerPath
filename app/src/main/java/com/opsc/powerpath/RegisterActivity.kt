// RegisterActivity.kt
package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.opsc.powerpath.databinding.ActivityRegisterBinding

//import com.opsc.powerpath.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var loginOp : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        loginOp = findViewById(R.id.loginOption)

        loginOp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {


                val firstName = binding.txtFname.text.toString().trim()
                val surname = binding.txtSname.text.toString().trim()
                val phoneNumber = binding.txtPhoneNumber.text.toString().trim()
                val email = binding.txtEmail.text.toString().trim()
                val password = binding.txtPassword.text.toString().trim()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            // Handle registration logic here
                            Toast.makeText(baseContext, "Registration Successful", Toast.LENGTH_SHORT).show()
                            // Navigate to onboarding activity
                            val intent = Intent(baseContext, CompleteActivity::class.java)
                            startActivity(intent)
                            finish();
                        } else {
                            // If sign-in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            }
        }

        binding.googleSignIn.setOnClickListener {
            // Handle Google Sign-In logic here
            Toast.makeText(this, "Google Sign-In Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = binding.txtFname.text.toString().trim()
        val surname = binding.txtSname.text.toString().trim()
        val phoneNumber = binding.txtPhoneNumber.text.toString().trim()
        val email = binding.txtEmail.text.toString().trim()
        val password = binding.txtPassword.text.toString().trim()
        val confirmPassword = binding.txtConfPassword.text.toString().trim()

        if (firstName.isEmpty()) {
            binding.txtFname.error = "First name is required"
            return false
        }

        if (surname.isEmpty()) {
            binding.txtSname.error = "Surname is required"
            return false
        }

        if (phoneNumber.isEmpty()) {
            binding.txtPhoneNumber.error = "Phone number is required"
            return false
        }

        if (email.isEmpty()) {
            binding.txtEmail.error = "Email is required"
            return false
        }

        if (password.isEmpty()) {
            binding.txtPassword.error = "Password is required"
            return false
        }

        if (confirmPassword.isEmpty()) {
            binding.txtConfPassword.error = "Confirm password is required"
            return false
        }

        if (password != confirmPassword) {
            binding.txtConfPassword.error = "Passwords do not match"
            return false
        }

        if (!binding.privacyPolicy.isChecked) {
            Toast.makeText(this, "You must agree to the privacy policy", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}