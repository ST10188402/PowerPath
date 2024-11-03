package com.opsc.powerpath

import android.R.attr.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProgressPage : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: GalleryAdapter
    private lateinit var galleryItems: MutableList<GalleryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.gallery_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        galleryItems = mutableListOf()

        adapter = GalleryAdapter(galleryItems)
        recyclerView.adapter = adapter

        val initialItems = listOf(
            GalleryItem(0, "2 June"),  // Placeholder, will update image later
            GalleryItem(0, "5 May")
        )
        adapter.addGalleryItems(initialItems)

        val comparePhotoCard = view.findViewById<CardView>(R.id.ComparePhotoCard)
        comparePhotoCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ComparisonFragment.newInstance("", ""))
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProgressPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}