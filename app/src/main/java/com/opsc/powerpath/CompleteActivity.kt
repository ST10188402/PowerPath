package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.opsc.powerpath.databinding.ActivityCompleteProfileBinding

class CompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompleteProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var date: String
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        // declare the date picker
        val genderSpinner: Spinner = binding.genderSpinner

        // Initialize the date picker
        binding.datePicker.init(2000, 1, 1, object : DatePicker.OnDateChangedListener {
            override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                // Show the toast whenever the date is changed
                date = binding.datePicker.dayOfMonth.toString() + "/" + binding.datePicker.month.toString() + "/" + binding.datePicker.year.toString()
                Toast.makeText(baseContext, date, Toast.LENGTH_SHORT).show()
            }
        })

        //Initialize the spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }
        //Set on click listener for the complete profile button
        binding.btnCompleteProfile.setOnClickListener {
         //if the inputs are valid
            if (validateInputs()) {
                //save the data to database
                SaveData()
                //navigate to the login activity
                val intent = Intent(this, SuccessActivity::class.java)
                startActivity(intent)
            }

        }
    }
    //Function to save the user data to the database
    private fun SaveData() {
        val user = auth.currentUser
        user?.let {
            val uid = it.uid
            val _gender = binding.genderSpinner.selectedItem.toString()
            val _dob = date
            val _weight = binding.weight.text.toString().trim()
            val _height = binding.height.text.toString().trim()

            val userHealthMap = hashMapOf(
                "user_id" to uid,
                "gender" to _gender,
                "date_of_birth" to _dob,
                "height" to _height,
                "weight" to _weight
            )

            database.reference.child("health_data").child(uid).setValue(userHealthMap)
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "User health data saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext, "Failed to save user health data", Toast.LENGTH_SHORT).show()
                }
        }
    }
    //Function to validate the user inputs
    private fun validateInputs(): Boolean {

        val valid = Valid()

        val gender = binding.genderSpinner.selectedItem.toString()
        val weight = binding.weight.text.toString().trim()
        val height = binding.height.text.toString().trim()
        val dateOfBirth  = date

       if(dateOfBirth.isEmpty()) {
           Toast.makeText(baseContext, "Date of Birth is required", Toast.LENGTH_SHORT).show()
           return false
       }
        if (!valid.ValidHeight(height, binding.height::setError)) {
            return false
        }
        if (!valid.ValidWeight(weight, binding.weight::setError)){
            return false
        }

        if (dateOfBirth.isEmpty()) {
            Toast.makeText(baseContext, "Date of Birth is required",Toast.LENGTH_SHORT).show()

            return false
        }

        if (weight.isEmpty()) {
            binding.weight.error = "Weight is required"
            return false
        }

        if (height.isEmpty()) {
            binding.height.error = "Height is required"
            return false
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}