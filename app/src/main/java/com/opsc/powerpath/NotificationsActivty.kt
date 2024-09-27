package com.opsc.powerpath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        recyclerView = findViewById(R.id.notifications_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Example notification data
        val notifications = listOf(
            Notification("Don’t miss your lowerbody workout", "About 3 hours ago", R.drawable.ic_notiwork),
            Notification("Oopsie, You have missed your workout", "3 April", R.drawable.ic_notiwork)
        )

        // Set up the adapter
        notificationAdapter = NotificationAdapter(notifications)
        recyclerView.adapter = notificationAdapter
    }
}
