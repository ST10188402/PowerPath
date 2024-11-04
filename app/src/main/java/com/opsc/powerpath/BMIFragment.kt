package com.opsc.powerpath

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.opsc.powerpath.Data.Models.User
import com.opsc.powerpath.Data.Models.WeightProgress

class BMIFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var addWeightButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bmi_item, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get the PieChart view
        pieChart = view.findViewById(R.id.donutChartView)
        addWeightButton = view.findViewById(R.id.addWeightButton)

        // Fetch user data and set up the PieChart with BMI data
        fetchUserDataAndSetupPieChart()

        // Set up the button click listener
        addWeightButton.setOnClickListener {
            showAddWeightDialog()
        }

        return view
    }

    private fun fetchUserDataAndSetupPieChart() {
        val userId = auth.currentUser!!.uid
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val user = document.toObject(User::class.java)
                user?.let {
                    val height = (it.height ?: 0f).toFloat()
                    val weight = (it.weight ?: 0f).toFloat()
                    val bmi = calculateBMI(height, weight)
                    setupPieChart(bmi)
                }
            } else {
                Toast.makeText(requireContext(), "No such user found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Failed to get user details: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateBMI(height: Float, weight: Float): Float {
        return if (height > 0) {
            weight / ((height / 100) * (height / 100))
        } else {
            0f
        }
    }

    private fun setupPieChart(bmi: Float) {
        val entries = mutableListOf<PieEntry>()
        val bmiCategory = when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Healthy"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obese"
        }
        entries.add(PieEntry(bmi, bmiCategory))
        entries.add(PieEntry(100 - bmi, "Remaining"))

        val dataSet = PieDataSet(entries, "BMI Data")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // Refresh the chart
    }

    private fun showAddWeightDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Weight")

        val input = EditText(requireContext())
        input.hint = "Enter weight"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val weight = input.text.toString().toFloatOrNull()
            if (weight != null) {
                saveWeightToDatabase(weight)
            } else {
                Toast.makeText(requireContext(), "Invalid weight", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveWeightToDatabase(weight: Float) {
        val userId = auth.currentUser!!.uid
        val weightProgress = WeightProgress(weight = weight)

        // Add the new weight entry to the weightProgress subcollection
        db.collection("users").document(userId).collection("weight-progress")
            .add(weightProgress)
            .addOnSuccessListener {
                // Update the user's weight in the main user document
                db.collection("users").document(userId)
                    .update("weight", weight)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Weight added and updated successfully", Toast.LENGTH_SHORT).show()
                        fetchUserDataAndSetupPieChart() // Refresh the chart with new data
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Failed to update user weight: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to add weight: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}