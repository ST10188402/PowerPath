package com.opsc.powerpath.Data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.opsc.powerpath.R

class MessagingService : FirebaseMessagingService() {
    private val CHANNEL_ID = "channel_id"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupFirestoreListeners()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message.notification?.title, message.notification?.body)
    }

    private fun showNotification(title: String?, body: String?) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "General Notifications"
            val descriptionText = "Channel for general notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupFirestoreListeners() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()

        // Listener for exercises collection
        val exercisesRef = db.collection("users").document(userId).collection("exercises")
        exercisesRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("MessagingService", "Listen failed.", e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                        val notification = dc.document.toObject(RemoteMessage.Notification::class.java)
                        showNotification(notification.title, notification.body)
                    }
                    DocumentChange.Type.REMOVED -> {
                        // Handle removed documents if needed
                    }
                }
            }
        }

        // Listener for workouts collection
        val workoutsRef = db.collection("users").document(userId).collection("workouts")
        workoutsRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("MessagingService", "Listen failed.", e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                        val notification = dc.document.toObject(RemoteMessage.Notification::class.java)
                        showNotification(notification.title, notification.body)
                    }
                    DocumentChange.Type.REMOVED -> {
                        // Handle removed documents if needed
                    }
                }
            }
        }
    }
}