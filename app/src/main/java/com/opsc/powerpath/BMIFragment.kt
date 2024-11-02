package com.opsc.powerpath

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class BMIFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bmi_item, container, false)

        // Get the PieChart view
        val pieChart: PieChart = view.findViewById(R.id.donutChartView)

        // Set up the PieChart with BMI data
        setupPieChart(pieChart)

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
}