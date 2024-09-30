package com.opsc.powerpath.Data.API

import com.opsc.powerpath.Data.Models.Exercise
import com.opsc.powerpath.Data.Models.User
import com.opsc.powerpath.Data.Models.WeightProgress
import com.opsc.powerpath.Data.Models.Workout
import retrofit2.Call
import retrofit2.http.*
import com.opsc.powerpath.Data.Models.WorkoutHistory
import com.opsc.powerpath.Utils.Util

interface IApi {

    @POST("/api/users")
    fun addUser(@Body user: User): Call<Void>

    // Add or update user's height and weight
    @PUT(Util.BASE_URL+"/api/users/{userId}/profile")
    fun updateUserProfile(@Path("userId") userId: String, @Body user: User): Call<Void>

    // Add weight progress
    @POST(Util.BASE_URL+"/api/users/{userId}/weight-progress")
    fun addWeightProgress(@Path("userId") userId: String, @Body weightProgress: WeightProgress): Call<Void>

    // Get all weight progress records for the user
    @GET(Util.BASE_URL+"/api/users/{userId}/weight-progress")
    fun getWeightProgress(@Path("userId") userId: String): Call<List<WeightProgress>>

    // Add a new workout for the user
    @POST(Util.BASE_URL+"/api/users/{userId}/workouts")
    fun addWorkout(@Path("userId") userId: String, @Body workout: Workout): Call<Void>

    // Get all workouts for the user
    @GET("/api/users/{userId}/workouts")
    fun getWorkouts(@Path("userId") userId: String): Call<List<Workout>>

    // Add a new exercise for the user
    @POST("/api/users/{userId}/exercises")
    fun addExercise(
        @Path("userId") userId: String,
        @Body exerciseData: Exercise
    ): Call<ApiResponse>

    // Get all exercises for the user
    @GET("/api/users/{userId}/exercises")
    fun getAllExercises(
        @Path("userId") userId: String
    ): Call<List<Exercise>>

    // Update a specific exercise for the user
    @PUT("/api/users/{userId}/exercises/{exerciseId}")
    fun updateExercise(
        @Path("userId") userId: String,
        @Path("exerciseId") exerciseId: String,
        @Body exerciseData: Exercise
    ): Call<ApiResponse>

    // Delete an exercise for the user
    @DELETE("/api/users/{userId}/exercises/{exerciseId}")
    fun deleteExercise(
        @Path("userId") userId: String,
        @Path("exerciseId") exerciseId: String
    ): Call<ApiResponse>

    // Schedule a workout
    @POST("/api/users/{userId}/workouts/{workoutId}/schedule")
    fun scheduleWorkout(
        @Path("userId") userId: String,
        @Path("workoutId") workoutId: String,
        @Body scheduleData: Workout
    ): Call<ApiResponse>

    // Record workout completion
    @POST("/api/users/{userId}/workouts/{workoutId}/history")
    fun recordWorkoutCompletion(
        @Path("userId") userId: String,
        @Path("workoutId") workoutId: String,
        @Body workoutHistoryData: WorkoutHistory
    ): Call<ApiResponse>

    // Get workout history
    @GET("/api/users/{userId}/workouts/{workoutId}/history")
    fun getWorkoutHistory(
        @Path("userId") userId: String,
        @Path("workoutId") workoutId: String
    ): Call<List<WorkoutHistory>>
}