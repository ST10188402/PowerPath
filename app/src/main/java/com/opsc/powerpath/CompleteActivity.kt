package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.opsc.powerpath.databinding.ActivityCompleteProfileBinding

class CompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompleteProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val genderSpinner: Spinner = binding.genderSpinner
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
        }
    }

    private fun validateInputs(): Boolean {
        val valid = Valid()

        val dateOfBirth = binding.dateOfBirth.text.toString().trim()
        val weight = binding.weight.text.toString().trim()
        val height = binding.height.text.toString().trim()


        if (!valid.ValidHeight(height, binding.height::setError)) {
            return false
        }
        if (!valid.ValidWeight(weight, binding.weight::setError)){
            return false
        }

        if (dateOfBirth.isEmpty()) {
            binding.dateOfBirth.error = "Date of Birth is required"
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

        return true
    }
}