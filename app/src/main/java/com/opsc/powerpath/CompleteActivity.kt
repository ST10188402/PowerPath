package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.opsc.powerpath.databinding.ActivityCompleteProfileBinding

class CompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompleteProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val genderSpinner: Spinner = binding.genderSpinner
        val datePicker = binding.datePicker
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }

        binding.btnCompleteProfile.setOnClickListener {
            if (validateInputs()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            binding.datePicker.init(2000, 1, 1, object : DatePicker.OnDateChangedListener {
                override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                    // Show the toast whenever the date is changed
                    val date = binding.datePicker.dayOfMonth.toString() + "/" + binding.datePicker.month.toString() + "/" + binding.datePicker.year.toString()
                    Toast.makeText(baseContext, date, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun validateInputs(): Boolean {
        val valid = Valid()

        val gender = binding.genderSpinner.selectedItem.toString()
        val weight = binding.weight.text.toString().trim()
        val height = binding.height.text.toString().trim()

        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year
        val dateOfBirth  = day.toString() + "/" + month.toString() + "/" + year.toString()

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