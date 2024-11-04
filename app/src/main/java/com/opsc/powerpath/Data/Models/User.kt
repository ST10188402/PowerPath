package com.opsc.powerpath.Data.Models

data class User(
    val created: String? = null,
    val surname: String? = null,
    val name: String? = null,
    val dateOfBirth: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val gender: String? = null,
    val uid: String? = null
) {
    // No-argument constructor required for Firestore
    constructor() : this(null, null, null, null, null, null, null, null)
}