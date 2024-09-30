package com.opsc.powerpath

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.opsc.powerpath.databinding.ActivityTakePhotoBinding


class TakePhotoFragment : Fragment() {
    private var _viewBinding: ActivityTakePhotoBinding? = null
    private lateinit var flash: Button
    private var isFlashOn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_take_photo, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flash = view.findViewById(R.id.btn_flash)
        flash.setOnClickListener {
            if (isFlashOn) {
                flash.setBackgroundResource(R.drawable.ic_flash_off)
            } else {
                flash.setBackgroundResource(R.drawable.ic_flash_on)
            }
            isFlashOn = !isFlashOn
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}