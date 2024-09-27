package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.google.firebase.database.database
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var regOp : LinearLayout

    private lateinit var googleSignIn: ImageView
    private lateinit var gso : GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    val database = Firebase.database

    private var trueUser = false

    private  var RC_SIGN_IN = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.txt_email)
        passwordEditText = findViewById(R.id.txt_password)
        loginButton = findViewById(R.id.btn_login)

        regOp = findViewById(R.id.registerOption)

        googleSignIn = findViewById(R.id.google_sign_in)
        googleSignIn = findViewById(R.id.google_sign_in)
        auth = FirebaseAuth.getInstance()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this , gso)


        googleSignIn.setOnClickListener {
            // Handle Google Sign-In logic here
            Toast.makeText(this, "Google Sign-In Clicked", Toast.LENGTH_SHORT).show()
            signIn();
        }



        regOp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        loginButton.setOnClickListener {
            if (validateInputs()) {
                // Handle login logic here
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                trueUser =  authenticateUser(email, password)

                if (trueUser) {
                    //val user = auth.currentUser
                    val intent = Intent(this, SuccessActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                else
                {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
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
                    if (user != null)                    {

                        // Check if the user is registered in the database
                        database.reference.child("users").child(user.uid).get().addOnSuccessListener {

                            if (it.exists())
                            {
                                // If the user is registered, navigate to SuccessActivity
                                // val intent = Intent(this, SuccessActivity::class.java)
                                // val intent = Intent(this, CompleteActivity::class.java)
                                val intent = Intent(this, TakePhotoActivity::class.java)
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

    private fun validateInputs(): Boolean {
        val valid = Valid()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            return false
        }

        if (!valid.isValidEmail(email)) {
            emailEditText.error  = "Invalid email format"
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error  = "Password is required"
            return false
        }

        if (!valid.isValidPassword(password)) {
            passwordEditText.error = "Password must be at least 8 characters long and contain at least one digit and one special character"
            return false
        }

        return true
    }


    private fun authenticateUser(email: String, password: String) : Boolean {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    trueUser = true
                    Toast.makeText(this, "Authentication successful.", Toast.LENGTH_SHORT).show()


                } else
                {
                    trueUser = false
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

        return trueUser

    }
}