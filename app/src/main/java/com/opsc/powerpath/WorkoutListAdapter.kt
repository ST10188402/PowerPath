package com.opsc.powerpath

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class WorkoutListAdapter(
    private val workouts: MutableList<Workout>
) : RecyclerView.Adapter<WorkoutListAdapter.WorkoutViewHolder>() {

    private val itemSelectedList = mutableListOf<Workout>()
    private var isSelectionMode = true

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutName: TextView = itemView.findViewById(R.id.txtTitle)
        val workoutDate: TextView = itemView.findViewById(R.id.txtDate)
        val workoutLocation: TextView = itemView.findViewById(R.id.txtLocation)
        // val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_card, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutName.text = workout.name
       // holder.workoutDate.text = formatWorkoutDate(workout.date)
       // holder.workoutLocation.text = workout.location

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                selectWorkout(holder, workout)
            } else {
                val intent = Intent(holder.itemView.context, WorkoutDetailsActivity::class.java)
                intent.putExtra("WORKOUT_ID", workout.id)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    private fun formatWorkoutDate(date: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        val parsedDate = inputDateFormat.parse(date)
        return if (parsedDate != null) {
            outputDateFormat.format(parsedDate)
        } else {
            date
        }
    }

    private fun selectWorkout(holder: WorkoutViewHolder, workout: Workout) {
        if (workout.selected) {
            itemSelectedList.remove(workout)
            workout.selected = false
        } else {
            itemSelectedList.add(workout)
            workout.selected = true
        }

        notifyItemChanged(holder.adapterPosition)
        Toast.makeText(holder.itemView.context, "Selected workout: ${workout.name}", Toast.LENGTH_SHORT).show()
    }

    private fun showAllCheckBoxes() {
        for (workout in workouts) {
            workout.selected = false
        }
        notifyDataSetChanged()
    }

    fun getSelectedWorkouts(): List<Workout> {
        return itemSelectedList
    }

    fun removeSelectedWorkouts() {
        workouts.removeAll(itemSelectedList)
        itemSelectedList.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }

    fun clearSelection() {
        for (workout in workouts) {
            workout.selected = false
        }
        itemSelectedList.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }
}