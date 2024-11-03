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
import com.opsc.powerpath.Data.Models.WeightProgress
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BMIFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bmi_item, container, false)

        // Get the PieChart view
        val pieChart: PieChart = view.findViewById(R.id.donutChartView)
        val addWeightButton: Button = view.findViewById(R.id.addWeightButton)

        // Set up the PieChart with BMI data
        setupPieChart(pieChart)

        // Set up the button click listener
        addWeightButton.setOnClickListener {
            showAddWeightDialog()
        }

        return view
    }

    private fun setupPieChart(pieChart: PieChart) {
        // Example BMI data
        val bmi = 22.0f // Replace with actual BMI value
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(bmi, "BMI"))
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
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val weightProgress = WeightProgress(weight = weight)

        val apiService = RetrofitInstance.api.addWeightProgress(userId, weightProgress)
        apiService.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Weight added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to add weight: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}