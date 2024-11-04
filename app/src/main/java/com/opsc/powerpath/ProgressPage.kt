package com.opsc.powerpath


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.Calendar
import java.util.Locale
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProgressPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())


        val galleryFragmentCurrent = GalleryFragment.newInstance(currentMonth)

        childFragmentManager.beginTransaction()
            .replace(R.id.gallery_container_current, galleryFragmentCurrent)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProgressPage().apply {
            arguments = Bundle().apply {
            }
        }
    }
}