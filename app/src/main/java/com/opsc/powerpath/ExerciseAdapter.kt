package com.opsc.powerpath

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.powerpath.Data.Models.Exercise

class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val onExerciseSelected: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(exercise: Exercise) {
            itemView.setOnClickListener {
                onExerciseSelected(exercise)
            }
            val exerciseName = itemView.findViewById<TextView>(R.id.exerciseNameTextView)
            val muscleGroup = itemView.findViewById<TextView>(R.id.muscleGroupTextView)
            exerciseName.text = exercise.name
            muscleGroup.text = exercise.muscleGroup

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_excercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}