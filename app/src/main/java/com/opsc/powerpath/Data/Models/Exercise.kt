package com.opsc.powerpath.Data.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    val id: String,
    val muscleGroup: String,
    val name: String
) : Parcelable