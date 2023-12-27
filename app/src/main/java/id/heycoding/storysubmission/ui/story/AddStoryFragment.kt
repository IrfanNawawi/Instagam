package id.heycoding.storysubmission.ui.story

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import id.heycoding.storysubmission.MainActivity
import id.heycoding.storysubmission.R
import id.heycoding.storysubmission.databinding.FragmentAddStoryBinding
import id.heycoding.storysubmission.utils.Preferences
import id.heycoding.storysubmission.utils.UploadStoryUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryFragment : Fragment() {

    private var fragmentAddStoryBinding: FragmentAddStoryBinding? = null
    private lateinit var addStoryViewModel: AddStoryViewModel

    private lateinit var currentPath: String
    private lateinit var userLoginPref: Preferences

    private var getFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddStoryBinding = FragmentAddStoryBinding.inflate(layoutInflater)
        return fragmentAddStoryBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userLoginPref = Preferences(requireContext())
        (activity as MainActivity).supportActionBar?.hide()
        bindObservers()
        initView()
        setupAccessibility()
    }

    private fun bindObservers() {
        addStoryViewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]

        addStoryViewModel.apply {
            message.observe(viewLifecycleOwner) {
                showMessage(it)
                findNavController().navigateUp()
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun initView() {
        fragmentAddStoryBinding?.apply {
            btnCamera.setOnClickListener { openCamera() }
            btnGallery.setOnClickListener { openGallery() }
            btnUpload.setOnClickListener { uploadStory() }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.let { intent.resolveActivity(it.packageManager) }

        activity?.let {
            UploadStoryUtils.createTempFile(it.application).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "id.heycoding.storysubmission",
                    it
                )
                currentPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                cameraIntentLauncher.launch(intent)
            }
        }
    }

    private val cameraIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            fragmentAddStoryBinding?.imgPreview?.setImageBitmap(result)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }

        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        galleryIntentLauncher.launch(chooser)

    }

    private val galleryIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = UploadStoryUtils.uriToFile(selectedImg, requireContext())
            getFile = myFile
            fragmentAddStoryBinding?.imgPreview?.setImageURI(selectedImg)
        }
    }

    private fun uploadStory() {
        if (getFile != null) {
            val file = UploadStoryUtils.reduceFileImage(getFile as File)
            val descriptionText = fragmentAddStoryBinding?.edtDesc?.text.toString()
            val description = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            if (userLoginPref.getLoginData().isLogin) {
                addStoryViewModel.uploadStoriesData(
                    userLoginPref.getLoginData().token,
                    imageMultipart,
                    description
                )
            } else {
                findNavController().navigate(R.id.action_addStoryFragment_to_loginFragment)
            }
        } else {
            showMessage("Silahkan masukkan gambar terlebih dahulu.")
        }

    }

    private fun setupAccessibility() {
        fragmentAddStoryBinding?.apply {
            imgPreview.contentDescription = getString(R.string.txt_image_desc)
            btnCamera.contentDescription = getString(R.string.txt_btn_camera_desc)
            btnGallery.contentDescription = getString(R.string.txt_btn_gallery_desc)
            tfDesc.contentDescription = getString(R.string.txt_column_desc)
            btnUpload.contentDescription = getString(R.string.txt_btn_upload_desc)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                showMessage("Permission is not granted")
            }
        }
    }


    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}