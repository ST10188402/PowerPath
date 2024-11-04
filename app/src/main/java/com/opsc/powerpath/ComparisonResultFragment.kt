package com.opsc.powerpath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.util.Log

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ComparisonResultFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_comparison_result_page, container, false)

        val statisticsButton = view.findViewById<Button>(R.id.statisticsButton)
        statisticsButton.setOnClickListener {
            Log.d("ComparisonResultFragment", "Statistics button clicked")
            val month1 = GlobalData.month1 ?: ""
            val month2 = GlobalData.month2 ?: ""
            if (month1.isNotEmpty() && month2.isNotEmpty()) {
                val fragment = ComparisonResultStatsFragment.newInstance(month1, month2)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host, fragment)
                    .addToBackStack(null)
                    .commit()
                Log.d("ComparisonResultFragment", "Navigating to ComparisonResultStatsFragment")
            } else {
                Log.e("ComparisonResultFragment", "GlobalData.month1 or GlobalData.month2 is null or empty")
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ComparisonResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}