package com.opsc.powerpath

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Find the button and apply the gradient background
        val button = view.findViewById<Button>(R.id.edit_button)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColors(intArrayOf(Color.parseColor("#983BCB"), Color.parseColor("#4023D7")))
        gradientDrawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM
        button.background = gradientDrawable

        return view
    }
}