package com.opsc.powerpath

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.opsc.powerpath.databinding.ImageRowBinding

interface OnImageClickListener {
    fun onImageClick(position: Int)
}

// Adapter class for handling multiple images in a RecyclerView
class MultipleImageAdapter(
    private val imageByteArrayList: MutableList<ByteArray?> = mutableListOf(),
    private val filenameList: MutableList<String?> = mutableListOf(),
    private val listener: OnImageClickListener
) : RecyclerView.Adapter<MultipleImageAdapter.ViewHolder>() {

    // ViewHolder class to hold the views for each item in the RecyclerView
    inner class ViewHolder(val binding: ImageRowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onImageClick(adapterPosition)
            }
        }
    }

    // Inflates the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ImageRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Binds the data to the views for each item in the RecyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val byteArray = imageByteArrayList[position]
            if (byteArray != null) {
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    // Returns the total number of items in the RecyclerView
    override fun getItemCount() = imageByteArrayList.size

    // Adds new items to the adapter and refreshes the RecyclerView
    fun addItems(imageByteArrays: List<ByteArray?>, filenames: List<String?>) {
        imageByteArrayList.clear()
        filenameList.clear()
        imageByteArrayList.addAll(imageByteArrays)
        filenameList.addAll(filenames)
        notifyDataSetChanged()
    }

    // Updates the existing items in the adapter and refreshes the RecyclerView
    fun updateItems(imageByteArrays: List<ByteArray?>, filenames: List<String?>) {
        imageByteArrayList.clear()
        filenameList.clear()
        imageByteArrayList.addAll(imageByteArrays)
        filenameList.addAll(filenames)
        notifyDataSetChanged()
    }

    // Returns the current list of image byte arrays
    fun returnItems(): MutableList<ByteArray?> {
        return imageByteArrayList
    }
}