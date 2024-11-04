package com.opsc.powerpath

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.widget.Toast
import com.opsc.powerpath.Data.API.IApi
import com.opsc.powerpath.Utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ProfileFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var language: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        fetchUserData(view)
        language = view.findViewById(R.id.languages_setting)
        language.setOnClickListener {
            changeLanguage()
            Toast.makeText(context, "Languages Setting clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun changeLanguage() {
        val languages = arrayOf("English", "Afrikaans", "Sesotho")
        val languageCodes = arrayOf("en", "af", "st")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Language")
        builder.setItems(languages) { dialog, which ->
            val selectedLanguageCode = languageCodes[which]
            val languageManager = LanguageManager(requireContext())
            languageManager.updateResource(selectedLanguageCode)
            Toast.makeText(context, "Language changed to ${languages[which]}", Toast.LENGTH_SHORT).show()
            activity?.recreate()
        }
        builder.show()
    }
    private fun fetchUserData(view: View) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val api = RetrofitInstance.api
            val call = api.getUserById(currentUser.uid)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        user = response.body()!!
                        if (user != null) {
                            populateViews(view, user)
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun getUserAge(dateOfBirth: String): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        val parts = dateOfBirth.split("/")
        dob.set(parts[2].toInt(), parts[0].toInt() - 1, parts[1].toInt())

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    private fun populateViews(view: View, healthData: User) {
        val username = view.findViewById<TextView>(R.id.userName)
        val height = view.findViewById<TextView>(R.id.height)
        val weight = view.findViewById<TextView>(R.id.weight)
        val age = view.findViewById<TextView>(R.id.age)

        username.text = healthData.name
        height.text = "${healthData.height} cm"
        weight.text ="${healthData.weight} kg"
        age.text = "${healthData.dateOfBirth?.let { getUserAge(it) }} yrs"
    }
}