package com.opsc.powerpath.Data.Models

data class User(
    val created: String,
    val surname: String,
    val name: String,
    val dateOfBirth: String,
    val height: Int,
    val weight: Int,
    val gender: String,
    val uid: String
)
