package com.opsc.powerpath

import ImageSaver
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opsc.powerpath.databinding.ActivityTakePhotoBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TakePhotoFragment : Fragment(), OnImageClickListener {
    private var _viewBinding: ActivityTakePhotoBinding? = null
    private lateinit var rvPhotos: RecyclerView
    private lateinit var adapter: MultipleImageAdapter
    private lateinit var camera: Button
    private lateinit var gallery: Button
    private lateinit var defaultholder: LinearLayout
    private lateinit var displayImage: ImageView

    private var imageHolder: ByteArray? = null

    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2

    // Lists to store image URIs and filenames
    private var imageByteArray = mutableListOf<ByteArray?>()
    private var fileNameList = mutableListOf<String?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_take_photo, container, false)

        displayImage = view.findViewById(R.id.imageHolder)
        defaultholder = view.findViewById(R.id.default_layout)
        adapter = MultipleImageAdapter(imageByteArray, fileNameList, this)
        rvPhotos = view.findViewById(R.id.rvPhotos)
        rvPhotos.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvPhotos.adapter = adapter

        return view
    }

    override fun onImageClick(position: Int) {
        val selectedImage = imageByteArray[position]
        imageHolder = selectedImage
        val bitmap = BitmapFactory.decodeByteArray(selectedImage, 0, selectedImage?.size ?: 0)
        displayImage.setImageBitmap(bitmap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        camera = view.findViewById(R.id.btn_camera)
        camera.setOnClickListener {
            showImagePickerOptions()
        }

        gallery = view.findViewById(R.id.btn_gallery)
        gallery.setOnClickListener {
            openGallery()
        }
    }

    private fun showImagePickerOptions() {
        checkCameraPermission()
    }


    private fun checkCameraPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Camera permission is required to take a picture.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        imageLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val uris = mutableListOf<Uri?>()
                    val filenames = mutableListOf<String?>()

                    data.clipData?.let { clipData ->
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            uris.add(uri)
                            filenames.add(getFilenameFromUri(requireContext(), uri))


                            val byteArray =
                                requireActivity().contentResolver.openInputStream(uri)?.readBytes()
                            if (byteArray != null && !imageByteArray.contains(byteArray)) {
                                imageByteArray.add(byteArray)
                                fileNameList.add("Camera Image")
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                    if (uris.isNotEmpty()) {
                        switchDisplay()
                    } else {
                        Toast.makeText(requireContext(), "No images selected", Toast.LENGTH_SHORT)
                            .show()
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "Canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun switchDisplay() {
        defaultholder.visibility = View.GONE
        rvPhotos.visibility = View.VISIBLE
    }

    private fun getFilenameFromUri(context: Context, uri: Uri): String? {
        val fileName: String?
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val imageUri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        imageUri
                    )
                    val imageSaver = ImageSaver(requireContext())
                    val filePath = imageSaver.saveImageToFile(requireContext(), bitmap)
                    if (filePath != null) {
                        Toast.makeText(
                            requireContext(),
                            "Image saved to $filePath",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


                CAMERA_REQUEST -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    val imageSaver = ImageSaver(requireContext())
                    val filePath = imageSaver.saveImageToFile(requireContext(), bitmap)
                    if (filePath != null) {
                        Toast.makeText(
                            requireContext(),
                            "Image saved to $filePath",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updateDisplayPicture(bitmap)
                }
            }
        }
    }

    private fun updateDisplayPicture(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        imageByteArray.add(byteArray)
        fileNameList.add("Camera Image")
        adapter.notifyDataSetChanged()
        displayImage.setImageBitmap(bitmap)
        switchDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}