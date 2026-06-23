package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CAMERA_PERMISSION_CODE
import com.app.adhyatmah.data.preferences.CAMERA_REQUEST_CODE
import com.app.adhyatmah.data.preferences.GALLERY_PERMISSION_CODE
import com.app.adhyatmah.data.preferences.IMAGE_PICK_CODE
import com.app.adhyatmah.data.preferences.IS_PROFILE_DATA
import com.app.adhyatmah.data.preferences.PROFILE_IMG
import com.app.adhyatmah.databinding.FragmentEditProfileBinding
import com.app.adhyatmah.domain.model.profile.edit_profile.edit_profile_request.EditProfileRequest
import com.app.adhyatmah.domain.model.profile.get_profile.Payload
import com.app.adhyatmah.presentation.ui.viewmodel.AuthViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getLength
import com.app.adhyatmah.utils.getString
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

    private val viewmodel by activityViewModels<AuthViewModel>()
    private var file: File? = null
    private var mimeType: String = ""
    private lateinit var token: String
    private lateinit var customerID: String
    private var isImageUpdated = false
    private var isTextUpdated = false
    private lateinit var oldProfileData: Payload

    override fun setLayout() = R.layout.fragment_edit_profile

    override fun initView(savedInstanceState: Bundle?) {
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).orEmpty()
        oldProfileData =
            Preferences.getCustomModelPreference(requireContext(), IS_PROFILE_DATA) ?: return
        customerID = oldProfileData.user?.id.orEmpty()

        setupUI()
        setObserver()
        keyBoardClose()
    }

    private fun setupUI() {
        binding.apply {
            emailSectionEt.isClickable = false
            nameSectionEt.setText(oldProfileData.user?.firstName)
            etLastName.setText(oldProfileData.user?.lastName)
            emailSectionEt.setText(oldProfileData.user?.email)
            phoneNumber.setText(oldProfileData.user?.phone)

            val imageUrl = Preferences.getStringPreference(requireContext(), PROFILE_IMG).orEmpty()
            if (imageUrl.isBlank() || imageUrl == "null") {
                img.setImageResource(R.drawable.placeholder_icon)
            } else {
                Glide.with(this@EditProfileFragment)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_icon)
                    .error(R.drawable.placeholder_icon)
                    .into(img)
            }

            backImg.setOnClickListener { findNavController().navigateUp() }
            cameraIcon.setOnClickListener { showBottomSheet() }

            continueBtn.setOnClickListener { onUpdateClick() }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun keyBoardClose() {
        binding.scrollView.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val currentFocusedView = requireActivity().currentFocus
                if (currentFocusedView is EditText) {
                    val imm =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
                }
            }
            false
        }
    }

    private fun onUpdateClick() {
        if (!validateInputs()) return

        val newFirst = binding.nameSectionEt.getString()
        val newLast = binding.etLastName.getString()
        val newPhone = binding.phoneNumber.getString()

        isTextUpdated = newFirst != oldProfileData.user?.firstName ||
                newLast != oldProfileData.user?.lastName ||
                newPhone != oldProfileData.user?.phone

        if (!isImageUpdated && !isTextUpdated) {
            findNavController().navigateUp()
            Toast.makeText(
                requireContext(),
                getString(R.string.no_changes_to_save), Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (isImageUpdated) {
            uploadProfileImage()
        } else {
            updateProfile("")
        }
    }

    private fun uploadProfileImage() {
        file?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", it.name, requestFile)
            val customerIdBody = customerID.toRequestBody("text/plain".toMediaTypeOrNull())
            viewmodel.uploadImageToServer(filePart, customerIdBody)
        } ?: Toast.makeText(
            requireContext(),
            getString(R.string.no_image_selected), Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateProfile(image: String) {
        val request = EditProfileRequest(
            accessToken = token,
            firstName = binding.nameSectionEt.getString(),
            lastName = binding.etLastName.getString(),
            email = binding.emailSectionEt.getString(),
            phone = binding.phoneNumber.getString(),
            image = image.ifEmpty {
                Preferences.getStringPreference(requireContext(), PROFILE_IMG).orEmpty()
            }
        )
        viewmodel.hitEditProfileData(request)
    }

    private fun validateInputs(): Boolean {
        return with(binding) {
            when {
                nameSectionEt.getString().isBlank() -> {
                    nameSectionEt.error =
                        getString(R.string.first_name_is_required); nameSectionEt.requestFocus(); false
                }

                etLastName.getString().isBlank() -> {
                    etLastName.error =
                        getString(R.string.last_name_is_required); etLastName.requestFocus(); false
                }

                phoneNumber.getString().isBlank() -> {
                    phoneNumber.error =
                        getString(R.string.phone_number_is_required); phoneNumber.requestFocus(); false
                }

                phoneNumber.getLength() < 7 -> {
                    phoneNumber.error =
                        getString(R.string.enter_a_valid_phone_number); phoneNumber.requestFocus(); false
                }

                else -> true
            }
        }
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.profile_permission_bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<View>(R.id.optionGallery)?.setOnClickListener {
            checkPermissionForGallery()
            bottomSheetDialog.dismiss()
        }

        view.findViewById<View>(R.id.optionCamera)?.setOnClickListener {
            checkPermissionForCamera()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showGoToSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.to_upload_a_profile_picture_we_need_access_to_your_camera_and_photos_please_go_to_app_settings_and_allow_the_permissions))
            .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_is_required_to_upload_profile_image),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireContext().packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun checkPermissionForCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    private fun checkPermissionForGallery() {
        val permission =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                android.Manifest.permission.READ_MEDIA_IMAGES
            else
                android.Manifest.permission.READ_EXTERNAL_STORAGE

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(permission), GALLERY_PERMISSION_CODE)
        } else {
            openGallery()
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            IMAGE_PICK_CODE -> {
                data?.data?.let { uri ->
                    Glide.with(this).load(uri).into(binding.img)
                    file = uriToFile(uri)
                    mimeType = getMimeTypeFromUri(uri)
                    isImageUpdated = true
                }
            }

            CAMERA_REQUEST_CODE -> {
                val bitmap = data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    Glide.with(this).load(it).into(binding.img)
                    file = bitmapToFile(it)
                    mimeType = "image/jpeg"
                    isImageUpdated = true
                }
            }
        }
    }

    private fun getMimeTypeFromUri(uri: Uri): String {
        return requireContext().contentResolver.getType(uri) ?: "image/jpeg"
    }

    private fun uriToFile(uri: Uri): File {
        val extension = getMimeTypeFromUri(uri).substringAfterLast("/").let { ".$it" }
        val file = File(requireContext().cacheDir, "temp_image$extension")
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return file
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (ifUserPermanentDe(permissions[0])) {
                showGoToSettingsDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_is_required), Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        when (requestCode) {
            CAMERA_PERMISSION_CODE -> launchCamera()
            GALLERY_PERMISSION_CODE -> openGallery()
        }
    }

    private fun ifUserPermanentDe(permission: String): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), permission/* android.Manifest.permission.CAMERA*/
        )
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        return file
    }

    private fun setObserver() {
        viewmodel.getEditProfileData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.code == 200) {
                        viewmodel.hitAPIProfileData(token)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            it.data?.message ?: getString(R.string.update_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        it.message ?: getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        viewmodel.getProfileData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.code == 200) {
                        Preferences.setCustomModelPreference(
                            requireContext(),
                            IS_PROFILE_DATA,
                            it.data.payload
                        )
                        findNavController().navigateUp()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.ERROR -> {
                    Snackbar.make(
                        requireView(),
                        it.message ?: getString(R.string.error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        viewmodel.uploadProfileImgData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.code == 200) {
                        Preferences.setStringPreference(
                            requireContext(),
                            PROFILE_IMG,
                            it.data.payload?.url
                        )
                        updateProfile(it.data.payload?.url ?: "")
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.ERROR -> {
                    Snackbar.make(
                        requireView(),
                        it.message ?: getString(R.string.upload_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        viewmodel.getProfileImgData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.code == 200) {
                        Preferences.setStringPreference(
                            requireContext(),
                            PROFILE_IMG,
                            it.data.payload.url
                        )
                        if (isTextUpdated) updateProfile("") else findNavController().navigateUp()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.ERROR -> {
                    Snackbar.make(requireView(), it.message ?: getString(R.string.fetch_error), Snackbar.LENGTH_SHORT)
                        .show()
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }
}