package com.opsc.powerpath

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class TimePickerActivity : AppCompatActivity() {

    private lateinit var selectedTimeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_picker)

        selectedTimeTextView = findViewById(R.id.selected_time)

        // Handle click on the time TextView to open TimePickerDialog
        selectedTimeTextView.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        // Get the current time
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Initialize TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                // Format the selected time and set it to the TextView
                val formattedTime = formatTime(selectedHour, selectedMinute)
                selectedTimeTextView.text = formattedTime
            },
            hour, minute, false
        )

        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12
        return String.format("%02d:%02d %s", formattedHour, minute, amPm)
    }
}