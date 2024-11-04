package com.opsc.powerpath.Data.Models

data class Workout(
    val id: String? = null,
    val name : String? = null,
    val sets: Int? = null,
    val reps: Int? = null
) {
    constructor() : this("", "", 0, 0)
}