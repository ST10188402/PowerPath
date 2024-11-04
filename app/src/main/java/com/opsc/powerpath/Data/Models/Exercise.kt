package com.opsc.powerpath.Data.Models


data class Exercise(
    val id: String? = null,
    val muscleGroup: String? = null,
    val name: String? = null
) {
    // No-argument constructor required for Firestore
    constructor() : this(null, null, null)
}