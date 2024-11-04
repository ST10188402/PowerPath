package com.opsc.powerpath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.DatePickerDialog
import android.widget.TextView
import java.util.*
import android.widget.Button
import android.widget.LinearLayout

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ComparisonFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var selectMonth1: TextView
    private lateinit var currentMonth1: TextView
    private lateinit var selectMonth2: TextView
    private lateinit var currentMonth2: TextView

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
        val view = inflater.inflate(R.layout.fragment_comparison_page, container, false)

        selectMonth1 = view.findViewById(R.id.selectMonth1)
        currentMonth1 = view.findViewById(R.id.currentMonth1)
        selectMonth2 = view.findViewById(R.id.selectMonth2)
        currentMonth2 = view.findViewById(R.id.currentMonth2)

        val selectMonth1Container = view.findViewById<LinearLayout>(R.id.selectMonth1Container)
        val selectMonth2Container = view.findViewById<LinearLayout>(R.id.selectMonth2Container)

        selectMonth1Container.setOnClickListener { showDatePickerDialog(currentMonth1, 1) }
        selectMonth2Container.setOnClickListener { showDatePickerDialog(currentMonth2, 2) }

        view.findViewById<Button>(R.id.compareButton).setOnClickListener {
            comparePictures()
        }

        return view
    }

    private fun showDatePickerDialog(targetTextView: TextView, monthIndex: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                targetTextView.text = selectedDate
                if (monthIndex == 1) {
                    GlobalData.month1 = selectedDate
                } else {
                    GlobalData.month2 = selectedDate
                }
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            11 -> "December"
            else -> ""
        }
    }

    private fun comparePictures() {
        val fragment = ComparisonResultFragment.newInstance(GlobalData.month1 ?: "", GlobalData.month2 ?: "")
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ComparisonFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}