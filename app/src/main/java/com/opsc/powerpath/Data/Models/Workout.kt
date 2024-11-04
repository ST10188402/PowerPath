package com.opsc.powerpath.Data.Models

data class Workout(
    var id: String? = null,
    val name: String? = null,
    val muscleGroup: String? = null,
    val exercises: List<Exercise>? = null
) {
    // No-argument constructor required for Firestore
    constructor() : this(null, null, null)
}