package com.opsc.powerpath

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.opsc.powerpath.Data.Models.User
import com.opsc.powerpath.Utils.RetrofitInstance
import com.opsc.powerpath.databinding.ActivityCompleteProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class CompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompleteProfileBinding
    private lateinit var auth: FirebaseAuth
    private var date: String = ""
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

        // Initialize the spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }
        // Set on click listener for the complete profile button
        binding.btnCompleteProfile.setOnClickListener {
            // if the inputs are valid
            if (validateInputs()) {
                // save the data to database
                SaveData()
                // navigate to the login activity
                val intent = Intent(this, SuccessActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Function to save the user data to the database
    private fun SaveData() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val name = intent.getStringExtra("FIRST_NAME")
        val lastname = intent.getStringExtra("SURNAME")
        val weight = binding.weight.text.toString().toInt()
        val height = binding.height.text.toString().toInt()
        val date = binding.datePicker.dayOfMonth.toString() + "/" + binding.datePicker.month.toString() + "/" + binding.datePicker.year.toString()
        val gender = binding.genderSpinner.selectedItem.toString()

        val user = User(
            uid = uid,
            gender = gender,
            dateOfBirth = date,
            created = Date().toString(),
            weight = weight,
            height = height,
            surname = lastname.toString(),
            name = name.toString()
        )

            val apiService = RetrofitInstance.api.addUser(user)
            apiService.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "User health data saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Failed to save data: ${response.message()}", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "COMPLETE ACTIVITY api called${response.message()}")
                        Log.e(TAG, "COMPLETE ACTIVITY api called${response.body()}")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(baseContext, "Failed to save user health data: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "COMPLETE ACTIVITY failed not api ${t.message}")
                }
            })
        }

    // Function to validate the user inputs
    private fun validateInputs(): Boolean {

        val valid = Valid()
        val gender = binding.genderSpinner.selectedItem.toString()
        val weight = binding.weight.text.toString().toInt()
        val height = binding.height.text.toString().toInt()
        val dateOfBirth = date

        if (dateOfBirth.isEmpty()) {
            Toast.makeText(baseContext, "Date of Birth is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!valid.ValidHeight(height, binding.height::setError)) {
            return false
        }
        if (!valid.ValidWeight(weight, binding.weight::setError)) {
            return false
        }

        if (dateOfBirth.isEmpty()) {
            Toast.makeText(baseContext, "Date of Birth is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (weight.equals(null)) {
            binding.weight.error = "Weight is required"
            return false
        }

        if (height.equals(null)) {
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
