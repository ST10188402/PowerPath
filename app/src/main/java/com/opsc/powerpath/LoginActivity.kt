package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var auth : FirebaseAuth
    private lateinit var regOp : LinearLayout
    private var trueUser : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.txt_email)
        passwordEditText = findViewById(R.id.txt_password)
        loginButton = findViewById(R.id.btn_login)
        regOp = findViewById(R.id.registerOption)

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
                    val user = auth.currentUser
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