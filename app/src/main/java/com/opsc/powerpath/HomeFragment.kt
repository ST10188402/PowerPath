package com.opsc.powerpath

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.opsc.powerpath.Data.Models.User
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    val CHANNEL_ID = "channel_id"
    private lateinit var userName: TextView
    private lateinit var btnNot: ImageView
    private lateinit var btnWorkout: Button
    val permissionArray = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    lateinit var notificationManager: NotificationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (checkSelfPermission(requireContext(), permissionArray[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), permissionArray, 200)
        }

        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "General Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        userName = view.findViewById(R.id.welcome_msg)
        getUserDetails()

        btnNot = view.findViewById(R.id.btnNot)
        btnNot.setOnClickListener {
            val intent = Intent(activity, NotificationsActivity::class.java)
            startActivity(intent)
        }

        btnWorkout = view.findViewById(R.id.btnWorkout)
        btnWorkout.setOnClickListener {
            val intent = Intent(activity, WorkoutActivity::class.java)
            startActivity(intent)
        }

        // Add BMIFragment to the HomeFragment
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.bmi, BMIFragment())
        transaction.replace(R.id.workoutProgress, HomeChartFragment())
        transaction.commit()

        return view
    }

    private fun getUserDetails() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val user = document.toObject(User::class.java)
                user?.let {
                    userName.text = "${it.name} ${it.surname}"
                }
            } else {
                Toast.makeText(requireContext(), "No such user found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Failed to get user details: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}