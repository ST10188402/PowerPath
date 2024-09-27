package com.opsc.powerpath

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.opsc.powerpath.databinding.ActivityTakePhotoBinding

class TakePhotoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTakePhotoBinding
    private lateinit var flash: Button
    private var isFlashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTakePhotoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        flash = findViewById(R.id.btn_flash)
        flash.setOnClickListener {
            if (isFlashOn) {
                flash.setBackgroundResource(R.drawable.btn_flash)
            } else {
                flash.setBackgroundResource(R.drawable.btn_flash_on)
            }
            isFlashOn = !isFlashOn
        }

        enableEdgeToEdge()
    }
}