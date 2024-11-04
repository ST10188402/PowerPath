package com.opsc.powerpath

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.opsc.powerpath.Data.Models.User
import java.util.Calendar

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var language: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var popUpNotificationSwitch: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("com.opsc.powerpath", Context.MODE_PRIVATE)

        fetchUserData(view)
        language = view.findViewById(R.id.languages_setting)
        language.setOnClickListener {
            changeLanguage()
            Toast.makeText(context, "Languages Setting clicked", Toast.LENGTH_SHORT).show()
        }

        popUpNotificationSwitch = view.findViewById(R.id.pop_up_notification)
        popUpNotificationSwitch.isChecked = sharedPreferences.getBoolean("biometricEnabled", true)
        popUpNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("biometricEnabled", isChecked).apply()
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
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)

            userRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        populateViews(view, it)
                    }
                } else {
                    Toast.makeText(requireContext(), "No such user found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to get user details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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

        username.text = healthData.name ?: "N/A"
        height.text = "${healthData.height ?: 0} cm"
        weight.text = "${healthData.weight ?: 0} kg"
        age.text = "${getUserAge(healthData.dateOfBirth ?: "01/01/1970")} yrs"
    }
}