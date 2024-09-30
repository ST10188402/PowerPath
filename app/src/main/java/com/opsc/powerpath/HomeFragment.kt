package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.Firebase
import com.google.firebase.database.database

class HomeFragment : Fragment() {
    private lateinit var userName: TextView
    private lateinit var btnNot: ImageView

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,

        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        userName = view.findViewById(R.id.welcome_msg)
        userName.text =  CurrentUser.name

        btnNot = view.findViewById(R.id.btnNot)
        btnNot.setOnClickListener {
          //  val intent = Intent(activity, NotificationsActivity::class.java)
           // startActivity(intent)
        }

        // Add BMIFragment to the HomeFragment
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.bmi, BMIFragment())
        transaction.commit()

        return view
    }
}