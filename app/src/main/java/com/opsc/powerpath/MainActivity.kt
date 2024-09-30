package com.opsc.powerpath

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {

    private lateinit var gsc : GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private var isUser : Boolean = true

    val database = Firebase.database

    //declare the request code
    private  var RC_SIGN_IN = 20


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_get_started)

        login()

        val btnGetStarted: Button = findViewById(R.id.btn_get_started)
        btnGetStarted.setOnClickListener {
            val intent = Intent(this, com.opsc.powerpath.Onboarding::class.java)
            startActivity(intent)

        }


    }
    private fun login()
    {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso
        gsc = GoogleSignIn.getClient(this, gso)

        // Check if the user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null)
        {
            // If the user is already signed in, authenticate with the obtained ID token
            auth(account.idToken!!)
        }
       signIn()
    }
    //Function to handle Google Sign-In
    private fun signIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    //Function to handle the result of the Google Sign-In
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
    //Function to authenticate the user for Google Sign-In | Register with Google
    private fun auth(idToken: String) {
        // Create a credential using the ID token
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)

        // Sign in with the credential
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // If sign-in is successful, get the current user
                    val user = auth.currentUser
                    CurrentUser.uid = user?.uid
                    if (user != null)
                    {
                        // Check if the user is registered in the database
                        database.reference.child("users").child(user.uid).get().addOnSuccessListener {
                            // If the user is registered, navigate to SuccessActivity
                            if (it.exists())
                            {
                                // If the user is registered, navigate to SuccessActivity
                                val intent = Intent(this, ContainerActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        }

                    }
                }
            }
    }

}