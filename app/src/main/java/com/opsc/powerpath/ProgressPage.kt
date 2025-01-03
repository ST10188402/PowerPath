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
import androidx.cardview.widget.CardView
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
        val currentMonth =
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        calendar.add(Calendar.MONTH, -1)
        val previousMonth1 =
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        calendar.add(Calendar.MONTH, -1)
        val previousMonth2 =
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        val galleryFragmentCurrent = GalleryFragment.newInstance(currentMonth)
        val galleryFragmentPrev1 = GalleryFragment.newInstance(previousMonth1)
        val galleryFragmentPrev2 = GalleryFragment.newInstance(previousMonth2)

        childFragmentManager.beginTransaction()
            .replace(R.id.gallery_container_current, galleryFragmentCurrent)
            .replace(R.id.gallery_container_prev1, galleryFragmentPrev1)
            .replace(R.id.gallery_container_prev2, galleryFragmentPrev2)
            .commit()

        val comparePhotoCard = view.findViewById<CardView>(R.id.ComparePhotoCard)
        comparePhotoCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host, ComparisonFragment.newInstance("", ""))
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProgressPage().apply {
            arguments = Bundle().apply {
            }
        }
    }
}