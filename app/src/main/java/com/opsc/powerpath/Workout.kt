package com.opsc.powerpath

data class Exercise(
    val muscleGroup: String = "",
    val name: String = ""
)

data class Workout(
    var id: String = "",
    var name: String = "",
    var duration: Int = 0,
    var calories: Int = 0,
    var selected: Boolean = false,
    var exercises: List<Exercise> = listOf()
)

{
    constructor() : this("", "", 0, 0, false, listOf())
}