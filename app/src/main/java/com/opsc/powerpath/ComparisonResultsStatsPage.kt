package com.opsc.powerpath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ComparisonResultsStatsPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComparisonResultsStatsPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comparison_results_stats_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the graph (Assuming you're using a library like GraphView)

        // Back to Home button functionality
        val buttonBackToHome = view.findViewById<Button>(R.id.button_back_to_home)
        buttonBackToHome.setOnClickListener {
            // Handle navigation to home
        }

        // Set progress values (These values can be updated dynamically)
        val progressLoseWeight = view.findViewById<ProgressBar>(R.id.progress_lose_weight)
        progressLoseWeight.progress = 67

        val progressPushStrength = view.findViewById<ProgressBar>(R.id.progress_push_strength)
        progressPushStrength.progress = 12

        val progressPullStrength = view.findViewById<ProgressBar>(R.id.progress_pull_strength)
        progressPullStrength.progress = 43

        val progressLegsStrength = view.findViewById<ProgressBar>(R.id.progress_legs_strength)
        progressLegsStrength.progress = 11
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ComparisonResultsStatsPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ComparisonResultsStatsPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}