package com.opsc.powerpath

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.Data.Models.Workout

class WorkoutAdapter(
    private val workouts: List<Workout>,
    private val onWorkoutSelected: (Workout, Int) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutName: TextView = itemView.findViewById(R.id.workoutNameTextView)
        val workoutSets: TextView = itemView.findViewById(R.id.setsEditText)
        val workoutReps: TextView = itemView.findViewById(R.id.repsTextView)

        fun bind(workout: Workout) {
            itemView.setOnClickListener {
                onWorkoutSelected(workout, adapterPosition)
            }
            workoutName.text = workout.name
            workoutSets.text = workout.sets.toString()
            workoutReps.text = workout.reps.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position])
    }

    private var selectedExercise: Exercise? = null

    fun getSelectedExercise(): Exercise? {
        return selectedExercise
    }

    override fun getItemCount() = workouts.size
}