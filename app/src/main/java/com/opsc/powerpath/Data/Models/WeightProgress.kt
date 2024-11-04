package com.opsc.powerpath.Data.Models


data class WeightProgress(
    val weight: Float = 0f
) {
    // No-argument constructor required for Firestore
    constructor() : this(0f)
}