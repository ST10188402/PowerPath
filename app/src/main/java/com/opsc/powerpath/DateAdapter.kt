package com.opsc.powerpath

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DateAdapter(
    private val context: Context,
    private val dateList: List<DateItem>,
    private val onDateSelected: (DateItem) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val dateItem = dateList[position]
        holder.dayOfWeekTextView.text = dateItem.dayOfWeek
        holder.dayOfMonthTextView.text = dateItem.dayOfMonth.toString()

        // Change background color for selected item
        holder.itemView.isSelected = position == selectedPosition

        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onDateSelected(dateItem)
        }
    }

    override fun getItemCount(): Int = dateList.size

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfWeekTextView: TextView = itemView.findViewById(R.id.day_of_week)
        val dayOfMonthTextView: TextView = itemView.findViewById(R.id.day_of_month)
    }
}

data class DateItem(val dayOfWeek: String, val dayOfMonth: Int)
