package com.opsc.powerpath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.DatePickerDialog
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import android.provider.MediaStore
import android.database.Cursor
import android.net.Uri
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ComparisonFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var selectMonth1: TextView
    private lateinit var currentMonth1: TextView
    private lateinit var selectMonth2: TextView
    private lateinit var currentMonth2: TextView
    private lateinit var comparisonContainer: LinearLayout

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

        selectMonth1Container.setOnClickListener { showDatePickerDialog(currentMonth1) }
        selectMonth2Container.setOnClickListener { showDatePickerDialog(currentMonth2) }

        view.findViewById<Button>(R.id.compareButton).setOnClickListener {
            comparePictures()
        }

        return view
    }

    private fun showDatePickerDialog(targetTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                targetTextView.text = selectedDate
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun showMonthPicker(targetTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, _, monthOfYear, _ ->
                val monthName = getMonthName(monthOfYear)
                targetTextView.text = monthName
            },
            year, month, 1
        )

        datePickerDialog.datePicker.findViewById<View>(
            resources.getIdentifier("day", "id", "android")
        )?.visibility = View.GONE

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
        val month1 = currentMonth1.text.toString()
        val month2 = currentMonth2.text.toString()

        if (month1.isEmpty() || month2.isEmpty()) {
            Toast.makeText(requireContext(), "Please select both months", Toast.LENGTH_SHORT).show()
            return
        }

        val picturesMonth1 = getPicturesForMonth(month1)
        val picturesMonth2 = getPicturesForMonth(month2)

        val fragment = ComparisonResultFragment.newInstance(picturesMonth1.toString(),
            picturesMonth2.toString()
        )
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getPicturesForMonth(month: String): List<Uri> {
        val pictures = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_TAKEN)
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} LIKE ?"
        val selectionArgs = arrayOf("%$month%")
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor: Cursor? = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                pictures.add(contentUri)
            }
        }

        return pictures
    }

    private fun displayPictures(picturesMonth1: List<Uri>, picturesMonth2: List<Uri>) {
        comparisonContainer.removeAllViews()

        for (i in picturesMonth1.indices) {
            val imageView1 = ImageView(requireContext())
            imageView1.setImageURI(picturesMonth1[i])
            comparisonContainer.addView(imageView1)

            if (i < picturesMonth2.size) {
                val imageView2 = ImageView(requireContext())
                imageView2.setImageURI(picturesMonth2[i])
                comparisonContainer.addView(imageView2)
            }
        }
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