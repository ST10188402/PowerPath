package com.opsc.powerpath

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.opsc.powerpath.Data.Models.User
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var userName: TextView
    private lateinit var btnNot: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        userName = view.findViewById(R.id.welcome_msg)
        getUserDetails()

        btnNot = view.findViewById(R.id.btnNot)
        btnNot.setOnClickListener {
            val intent = Intent(activity, NotificationsActivity::class.java)
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
        val apiService = RetrofitInstance.api.getUserById(userId)
        apiService.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        userName.text = "${it.name} ${it.surname}"
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to get user details: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}