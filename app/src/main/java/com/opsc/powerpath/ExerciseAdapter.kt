package com.opsc.powerpath

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.powerpath.Data.Models.Exercise

class ExerciseAdapter(private val exercises: MutableList<Exercise>) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseNameTextView: TextView = itemView.findViewById(R.id.exerciseNameTextView)
        val muscleGroupTextView: TextView = itemView.findViewById(R.id.muscleGroupTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_excercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseNameTextView.text = exercise.name
        holder.muscleGroupTextView.text = exercise.muscleGroup

        holder.itemView.setOnLongClickListener {
            exercises.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, exercises.size)
            true
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}