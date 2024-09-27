package com.opsc.powerpath
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var recyclerViewDates: RecyclerView
    private lateinit var monthYearTextView: TextView

    private val dateList = mutableListOf<DateItem>()
    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        recyclerViewDates = findViewById(R.id.recycler_view_dates)
        monthYearTextView = findViewById(R.id.month_year)

        // Set up RecyclerView
        recyclerViewDates.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        updateCalendar()

        findViewById<ImageView>(R.id.prev_month).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        findViewById<ImageView>(R.id.next_month).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }

    private fun updateCalendar() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearTextView.text = dateFormat.format(calendar.time)

        // Clear previous dates
        dateList.clear()

        // Set the calendar to the first day of the current month
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDays) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            dateList.add(DateItem(dayOfWeek, day))
        }

        // Set adapter with the updated dates
        val adapter = DateAdapter(this, dateList) { selectedDate ->
            // Handle date selection here
            // e.g., Toast or update another part of the UI
        }
        recyclerViewDates.adapter = adapter
    }
}
