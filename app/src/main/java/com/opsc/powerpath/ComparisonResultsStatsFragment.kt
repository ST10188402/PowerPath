package com.opsc.powerpath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import com.google.gson.Gson
import com.opsc.powerpath.Data.Models.Workout
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ComparisonResultStatsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comparison_results_stats_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchWorkoutProgress(view)
    }

    private fun fetchWorkoutProgress(view: View) {
        val userId = "your_user_id" // Replace with actual user ID
        val url = "https://your-api-url.com/api/users/$userId/workouts"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to retrieve workout progress", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.string()
                    val workouts = Gson().fromJson(responseData, Array<Workout>::class.java).toList()

                    activity?.runOnUiThread {
                        displayWorkoutProgress(view, workouts)
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to retrieve workout progress", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun displayWorkoutProgress(view: View, workouts: List<Workout>) {
        val exercisesTextView = view.findViewById<TextView>(R.id.pull_strength_text)

        val exercisesText = workouts.joinToString(separator = "\n") { workout ->
            workout.exercises.joinToString(separator = ", ") { exercise ->
                exercise.toString()
            }
        }

        exercisesTextView.text = exercisesText
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ComparisonResultStatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}