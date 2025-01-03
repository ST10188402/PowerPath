package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.opsc.powerpath.Data.Models.User
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var regOp: LinearLayout

    private lateinit var googleSignIn: ImageView
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    val database = Firebase.database

    private var RC_SIGN_IN = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.txt_email)
        passwordEditText = findViewById(R.id.txt_password)
        loginButton = findViewById(R.id.btn_login)

        regOp = findViewById(R.id.registerOption)

        googleSignIn = findViewById(R.id.google_sign_in)

        // Create a GoogleSignInOptions object with the default sign-in options
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Create a GoogleSignInClient object with the GoogleSignInOptions object
        gsc = GoogleSignIn.getClient(this, gso)

        // Set an onClickListener for the register option
        regOp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        // Set an onClickListener for the Google Sign-In button
        googleSignIn.setOnClickListener {
            // Handle Google Sign-In logic here
            Toast.makeText(this@LoginActivity, "Google Sign-In Clicked", Toast.LENGTH_SHORT).show()
            signIn()
        }

        // Set an onClickListener for the login button
        loginButton.setOnClickListener {
            // if the user inputs are valid
            if (validateInputs()) {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                // Authenticate the user
                authenticateUser(email, password) { isAuthenticated ->
                    // If the user is authenticated, get the user data
                    if (isAuthenticated) {
                        val user = auth.currentUser
                        user?.let {
                            val userId = user.uid
                            val apiService = RetrofitInstance.api.getUserById(userId)
                            apiService.enqueue(object : Callback<User> {
                                override fun onResponse(call: Call<User>, response: Response<User>) {
                                    if (response.isSuccessful) {
                                        val user = response.body()
                                        if (user != null) {
                                            val intent = Intent(this@LoginActivity, SuccessActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(this@LoginActivity, "User data not found", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(this@LoginActivity, "Failed to get user details: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<User>, t: Throwable) {
                                    Toast.makeText(this@LoginActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                    // If the user is not authenticated, show a toast message
                    else {
                        Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Method to handle the Google Sign-In logic
    private fun signIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Method to handle the result of the sign-in attempt
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
                Toast.makeText(this@LoginActivity, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to authenticate the user for normal login
    private fun authenticateUser(email: String, password: String, callback: (Boolean) -> Unit) {
        // Authenticate the user with the provided email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // If the task is successful, show a toast message indicating authentication success
                if (task.isSuccessful) {
                    callback(true)
                    Toast.makeText(this@LoginActivity, "Authentication successful.", Toast.LENGTH_SHORT).show()
                }
                // If the task is not successful, show a toast message indicating authentication failure
                else {
                    callback(false)
                    Toast.makeText(this@LoginActivity, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Method to authenticate the user for SSO login / registration
    private fun auth(idToken: String) {
        // Create a credential using the ID token
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        // Sign in with the credential
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // If sign-in is successful, get the current user
                    val user = auth.currentUser
                    if (user != null) {
                        // Check if the user is registered in the database
                        database.reference.child("users").child(user.uid).get().addOnSuccessListener {
                            if (it.exists()) {
                                // If the user is registered, navigate to SuccessActivity
                                val intent = Intent(this@LoginActivity, SuccessActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else if (!it.exists()) {
                                // Navigate to Complete Registration activity
                                val intent = Intent(this@LoginActivity, CompleteRegistration::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                } else {
                    // If sign-in fails, display a message to the user
                    Toast.makeText(this@LoginActivity, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Method to validate the user inputs
    private fun validateInputs(): Boolean {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return false
        }
        return true
    }
}