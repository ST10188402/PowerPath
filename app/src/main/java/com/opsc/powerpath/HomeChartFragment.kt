package com.opsc.powerpath

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.opsc.powerpath.Data.Models.WeightProgress
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeChartFragment : Fragment() {

    private lateinit var lineChartView: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_chart, container, false)

        // Get the LineChart view
        lineChartView = view.findViewById(R.id.lineChartView)

        // Set up the LineChart with initial data
        setupLineChart()

        return view
    }

    private fun setupLineChart() {
        fetchDataAndDisplayChart()
    }

    private fun fetchDataAndDisplayChart() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val apiService = RetrofitInstance.api.getWeightProgress(userId)
        apiService.enqueue(object : Callback<List<WeightProgress>> {
            override fun onResponse(call: Call<List<WeightProgress>>, response: Response<List<WeightProgress>>) {
                if (response.isSuccessful) {
                    val weightProgressList = response.body()
                    weightProgressList?.let {
                        val entries = mutableListOf<Entry>()
                        for ((index, progress) in it.withIndex()) {
                            entries.add(Entry(index.toFloat(), progress.weight))
                        }
                        val dataSet = LineDataSet(entries, "Weight Progress")
                        val data = LineData(dataSet)
                        lineChartView.data = data
                        lineChartView.invalidate()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to get weight progress: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<WeightProgress>>, t: Throwable) {
                Toast.makeText(requireContext(), "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}