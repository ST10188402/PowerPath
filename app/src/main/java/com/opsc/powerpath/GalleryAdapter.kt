package com.opsc.powerpath

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GalleryAdapter(private var galleryItems: MutableList<GalleryItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || galleryItems[position].date != galleryItems[position - 1].date) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_section_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_item, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(galleryItems[position].date)
        } else {
            (holder as ItemViewHolder).bind()  // No image yet, so bind placeholder
        }
    }

    override fun getItemCount(): Int = galleryItems.size

    // Method to update the list dynamically
    fun addGalleryItems(newItems: List<GalleryItem>) {
        galleryItems.addAll(newItems)
        notifyDataSetChanged()  // Update the RecyclerView when new data is added
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTextView: TextView = itemView as TextView

        fun bind(date: String) {
            headerTextView.text = date
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.gallery_image)

        fun bind() {
            // Since no images are added yet, the placeholder is used
            imageView.setImageResource(R.drawable.placeholderimg)
        }
    }
}
