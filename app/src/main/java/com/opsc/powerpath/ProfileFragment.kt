package com.opsc.powerpath

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.opsc.powerpath.Data.Models.User

class ProfileFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        fetchUserData(view)

        return view
    }

    private fun fetchUserData(view: View) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            database.child("health_data").orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val healthData = dataSnapshot.getValue(User::class.java)
                    if (healthData != null) {
                        populateViews(view, healthData)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }
    }

    private fun populateViews(view: View, healthData: User) {
        val username = view.findViewById<TextView>(R.id.userName)
        val height = view.findViewById<TextView>(R.id.height)
        val weight = view.findViewById<TextView>(R.id.weight)
        val age = view.findViewById<TextView>(R.id.age)

        username.text = healthData.name
        height.text = healthData.height.toString()
        weight.text = healthData.weight.toString()
        age.text = healthData.dateOfBirth.toString()
    }
}