package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.opsc.powerpath.databinding.ActivityCompleteRegistrationBinding


class CompleteRegistration: AppCompatActivity() {

    private lateinit var binding: ActivityCompleteRegistrationBinding
    private lateinit var auth : FirebaseAuth
    val database = Firebase.database



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCompleteRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupUI()


    }

    //Function to set up the UI
    private fun setupUI() {
        binding

        binding.btnNext.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(baseContext, "Registration Complete", Toast.LENGTH_SHORT).show()
                // Navigate to onboarding activity
                val intent = Intent(baseContext, CompleteActivity::class.java)
                intent.putExtra("FIRST_NAME", binding.txtFname.text.toString())
                intent.putExtra("SURNAME", binding.txtSname.text.toString())
                startActivity(intent)
                finish();


            } else {
                Toast.makeText(baseContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun validateInputs(): Boolean
    {
        val valid = Valid()
        val firstName = binding.txtFname.text.toString().trim()
        val surname = binding.txtSname.text.toString().trim()
        val phoneNumber = binding.txtPhoneNumber.text.toString().trim()

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

        if (!binding.privacyPolicy.isChecked) {
            Toast.makeText(this, "You must agree to the privacy policy", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!valid.isValidPhoneNumber(phoneNumber)) {
            binding.txtPhoneNumber.error = "Phone number must be 10 digits long"
            return false
        }

        return true
    }

}