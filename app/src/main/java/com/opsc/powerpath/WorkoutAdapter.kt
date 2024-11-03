package com.opsc.powerpath

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.Data.Models.Workout

class WorkoutAdapter(private val workouts: List<Workout>) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutNameTextView: TextView = itemView.findViewById(R.id.workoutNameTextView)
        val setsTextView: TextView = itemView.findViewById(R.id.setsEditText)
        val repsTextView: TextView = itemView.findViewById(R.id.repsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutNameTextView.text = workout.name
        holder.setsTextView.text = "Sets: ${workout.sets}"
        holder.repsTextView.text = "Reps: ${workout.reps}"
    }

    private var selectedExercise: Exercise? = null

    fun getSelectedExercise(): Exercise? {
        return selectedExercise
    }

    override fun getItemCount() = workouts.size
}