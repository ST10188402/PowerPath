package com.opsc.powerpath

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.Data.Models.Workout

class WorkoutAdapter(private val exercises: List<Exercise>) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutNameTextView: TextView = itemView.findViewById(R.id.exerciseNameTextView)
        val muscleGroupTextView: TextView = itemView.findViewById(R.id.muscleGroupTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_excercise, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.workoutNameTextView.text = exercise.name
        holder.muscleGroupTextView.text = exercise.muscleGroup
    }

    override fun getItemCount() = exercises.size
}