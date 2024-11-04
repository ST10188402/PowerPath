package com.opsc.powerpath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ComparisonResultStatsFragment : Fragment() {
    private var month1: String? = null
    private var month2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            month1 = it.getString(ARG_PARAM1)
            month2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comparison_results_stats_page, container, false)
        fetchWorkoutProgress(view)
        return view
    }

    private fun fetchWorkoutProgress(view: View) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            val weightProgressRef = db.collection("users").document(userId).collection("weight-progress")

            val date1 = month1 ?: ""
            val date2 = month2 ?: ""

            if (date1.isNotEmpty() && date2.isNotEmpty()) {
                weightProgressRef.document(date1).get()
                    .addOnSuccessListener { document1 ->
                        val weight1 = document1.getDouble("weight")?.toFloat() ?: 0f

                        weightProgressRef.document(date2).get()
                            .addOnSuccessListener { document2 ->
                                val weight2 = document2.getDouble("weight")?.toFloat() ?: 0f
                                compareWeights(view, weight1, weight2)
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(requireContext(), "Failed to retrieve weight for $date2", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Failed to retrieve weight for $date1", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Dates are not properly set", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun compareWeights(view: View, weight1: Float, weight2: Float) {
        val month1TextView = view.findViewById<TextView>(R.id.month1)
        val month2TextView = view.findViewById<TextView>(R.id.month2)
        val progressBar = view.findViewById<ProgressBar>(R.id.lose_weight_progress)

        month1TextView.text = "Weight on $month1: $weight1 kg"
        month2TextView.text = "Weight on $month2: $weight2 kg"

        val weightDifference = weight1 - weight2
        val progress = (Math.abs(weightDifference) / weight1 * 100).toInt()

        if (weightDifference > 0) {
            // User lost weight
            progressBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_light),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            // User gained weight
            progressBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_light),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        progressBar.progress = progress.coerceIn(0, 100)
    }

    companion object {
        @JvmStatic
        fun newInstance(month1: String, month2: String) =
            ComparisonResultStatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, month1)
                    putString(ARG_PARAM2, month2)
                }
            }
    }
}

data class WeightProgress(
    val date: String = "",
    val weight: Float = 0f
)