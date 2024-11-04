package com.opsc.powerpath

import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GalleryFragment : Fragment() {
    private lateinit var adapter: GalleryAdapter
    private lateinit var rvPhotos: RecyclerView
    private lateinit var txtMonth : TextView
    private var imageByteArray = mutableListOf<ByteArray?>()
    private var fileNameList = mutableListOf<String?>()
    private var month: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            month = it.getString(ARG_MONTH)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtMonth = view.findViewById(R.id.txtMonth)
       // txtMonth.text = month
        txtMonth.text = getString(R.string.this_month)
        rvPhotos = view.findViewById(R.id.gallery_recycler_view)
        rvPhotos.layoutManager = GridLayoutManager(context, 3)

        adapter = GalleryAdapter(imageByteArray, fileNameList)
        rvPhotos.adapter = adapter

        loadImagesFromDirectory()
    }

    private fun loadImagesFromDirectory() {
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Power Path")
        if (directory.exists()) {
            val files = directory.listFiles { file -> file.extension == "png" || file.extension == "jpg" }
            if (files != null) {
                val filesByMonth = files.groupBy { file ->
                    val lastModified = file.lastModified()
                    val calendar = Calendar.getInstance().apply { timeInMillis = lastModified }
                    calendar.get(Calendar.MONTH)
                }

                filesByMonth.forEach { (month, filesInMonth) ->
                    // Process files for each month
                    filesInMonth.forEach { file ->
                        val byteArray = file.readBytes()
                        if (!imageByteArray.contains(byteArray)) {
                            imageByteArray.add(byteArray)
                            fileNameList.add(file.name)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "No images found in the directory", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Directory does not exist", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_MONTH = "month"

        @JvmStatic
        fun newInstance(month: String) =
            GalleryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MONTH, month)
                }
            }
    }
}