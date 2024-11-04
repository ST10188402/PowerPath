package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.opsc.powerpath.Data.Models.WeightProgress

class HomeChartFragment : Fragment() {

    private lateinit var lineChartView: LineChart
    private lateinit var signOutBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_chart, container, false)

        // Get the LineChart view
        lineChartView = view.findViewById(R.id.lineChartView)
        signOutBtn = view.findViewById(R.id.signOutBtn)

        // Set up the LineChart with initial data
        setupLineChart()

        // Set up the sign-out button
        signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }

    private fun setupLineChart() {
        fetchDataAndDisplayChart()
    }

    private fun fetchDataAndDisplayChart() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val weightProgressRef = db.collection("users").document(userId).collection("weight-progress")

        weightProgressRef.get().addOnSuccessListener { documents ->
            val weightProgressList = mutableListOf<WeightProgress>()
            for (document in documents) {
                val weightProgress = document.toObject(WeightProgress::class.java)
                weightProgressList.add(weightProgress)
            }
            displayChart(weightProgressList)
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Failed to get weight progress: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayChart(weightProgressList: List<WeightProgress>) {
        val entries = mutableListOf<Entry>()
        for ((index, progress) in weightProgressList.withIndex()) {
            entries.add(Entry(index.toFloat(), progress.weight))
        }
        val dataSet = LineDataSet(entries, "Weight Progress")
        val data = LineData(dataSet)
        lineChartView.data = data
        lineChartView.invalidate()
    }
}