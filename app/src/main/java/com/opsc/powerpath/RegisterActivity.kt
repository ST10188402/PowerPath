// RegisterActivity.kt
package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.opsc.powerpath.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var loginOp : LinearLayout
    private lateinit var googleSignIn: ImageView
    private lateinit var gso : GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient
    val database = Firebase.database


    private  var RC_SIGN_IN = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginOp = findViewById(R.id.loginOption)

        googleSignIn = findViewById(R.id.google_sign_in)
        auth = FirebaseAuth.getInstance()


         gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this , gso)

        setupUI()

        binding.googleSignIn.setOnClickListener {
            // Handle Google Sign-In logic here
            Toast.makeText(this, "Google Sign-In Clicked", Toast.LENGTH_SHORT).show()
            signIn();
        }

        loginOp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun signIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request code matches the sign-in request code
        if (requestCode == RC_SIGN_IN) {
            // Retrieve the sign-in task from the intent data
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Attempt to get the GoogleSignInAccount from the task
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

                // If successful, authenticate with the obtained ID token
                auth(account.idToken!!)

            } catch (e: ApiException) {
                // If an exception occurs, show a toast message indicating sign-in failure
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun auth(idToken: String) {
        // Create a credential using the ID token
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)

        // Sign in with the credential
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // If sign-in is successful, get the current user
                    val user = auth.currentUser
                    if (user != null)
                    {

                        // Check if the user is registered in the database
                       database.reference.child("users").child(user.uid).get().addOnSuccessListener {

                            if (it.exists())
                            {
                                // If the user is registered, navigate to SuccessActivity
                                val intent = Intent(this, SuccessActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else if (!it.exists())
                            {
                                val map = hashMapOf(
                                    "id" to user.uid,
                                    "name" to user.displayName,
                                    "profile" to user.photoUrl.toString()
                                )

                                // Save the user data to Firebase  Database
                                database.reference.child("users").child(user.uid).setValue(map)
                                // Handle registration logic here
                                Toast.makeText(baseContext, "Registration Successful", Toast.LENGTH_SHORT)
                                    .show()
                                // Navigate to onboarding activity
                                val intent = Intent(baseContext, CompleteActivity::class.java)
                                startActivity(intent)
                                finish();
                            }
                        }

                    }
                } else {
                    // If sign-in fails, display a message to the user
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
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

    }

    private fun validateInputs(): Boolean
    {
        val valid = Valid()
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

        if (!valid.isValidEmail(email)) {
            binding.txtEmail.error = "Invalid email format"
            return false
        }

        if (password.isEmpty()) {
            binding.txtPassword.error = "Password is required"
            return false
        }

        if (!valid.isValidPassword(password)) {
            binding.txtPassword.error = "Password must be at least 8 characters long and contain at least one digit and one special character"
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
        if (!valid.isValidPhoneNumber(phoneNumber)) {
            binding.txtPhoneNumber.error = "Invalid phone number format"
            return false
        }

        return true
    }
}