package com.app.adhyatmah.presentation.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.EMAIL_ID1
import com.app.adhyatmah.data.preferences.FCM_TOKEN
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.IS_PROFILE_DATA
import com.app.adhyatmah.databinding.FragmentSignUpBinding
import com.app.adhyatmah.domain.model.auth.SignUpRequest
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.viewmodel.AuthViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var isPasswordVisible = false

    override fun setLayout(): Int {
        return R.layout.fragment_sign_up
    }

    override fun initView(savedInstanceState: Bundle?) {
        subFun()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    fun subFun() {
        binding.loginPage.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        setObserver()

        binding.togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_off)
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_on)
            }
            binding.etPassword.setSelection(binding.etPassword.text!!.length)
        }
        hitSignUpAPi()
        privacyPolicy()
        keyBoardClose()

        binding.togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_off)
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_on)
            }
            binding.etPassword.setSelection(binding.etPassword.text!!.length)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun keyBoardClose() {
        binding.scrollV.setOnTouchListener { _, motionEvent ->
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

    fun hitSignUpAPi() {
        binding.signUpBtn.setOnClickListener {
            val firstName = binding.firstName.getString()
            val lastName = binding.etLastName.getString()
            val email = binding.etEmail.getString()
            val password = binding.etPassword.getString()
            val phone = binding.etPhoneNumber.getString()
            val role = "user"

            if (!isValidName(firstName)) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_your_valid_first_name),
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_your_first_name),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (!isValidName(lastName)) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_your_last_name), Toast.LENGTH_SHORT
                )
                    .show()

                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_your_email),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener

            }

            if (!isValidEmail(email)) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.invalid_email_format),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_your_phone_number),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener

            }
            if (phone.length < 10) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_a_valid_phone_number),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener

            }

            if (password.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_password), Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.password_must_be_at_least_6_characters),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val fcmToken = Preferences.getStringPreference(requireContext(), FCM_TOKEN)
            val request = SignUpRequest(
                firstName,
                lastName,
                email,
                password,
                phone = phone,
                role = role,
                deviceType = "android",
                deviceToken = fcmToken
            )
            authViewModel.getSignUpData(request)
        }

    }

    fun privacyPolicy() {
        val terms = getString(R.string.terms_of_services)
        val privacy = getString(R.string.privacy_policy)
        val fullText =
            getString(R.string.by_signing_up_you_agree_to_our_terms_of_services_and_privacy_policy)

        val spannableString = SpannableString(fullText)

        val startTerms = fullText.indexOf(terms)
        val endTerms = startTerms + terms.length

        val startPrivacy = fullText.indexOf(privacy)
        val endPrivacy = startPrivacy + privacy.length

        if (startTerms >= 0 && endTerms <= fullText.length) {
            val termsSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val bundle = Bundle()
                    bundle.putString("terms", "2")
                    findNavController().navigate(
                        R.id.action_signUpFragment_to_termConditionFragment,
                        bundle
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.isFakeBoldText = true
                    ds.color = ContextCompat.getColor(requireContext(), R.color.black)
                }
            }
            spannableString.setSpan(
                termsSpan,
                startTerms,
                endTerms,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        if (startPrivacy >= 0 && endPrivacy <= fullText.length) {
            val privacySpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().navigate(R.id.action_signUpFragment_to_termConditionFragment)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.isFakeBoldText = true
                    ds.color = ContextCompat.getColor(requireContext(), R.color.black)
                }
            }
            spannableString.setSpan(
                privacySpan,
                startPrivacy,
                endPrivacy,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.term.text = spannableString
        binding.term.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setObserver() {
        authViewModel.getSignupData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        201, 200 -> {
                            val data = it.data.payload
                            val token = it.data.payload.accessToken
                            authViewModel.hitAPIProfileData(token)
                            Preferences.setStringPreference(requireContext(), ACCESS_TOKEN, token)
                            Preferences.setCustomModelPreference(
                                requireContext(),
                                IS_PROFILE_DATA,
                                data
                            )
                            Preferences.setStringPreference(requireContext(), IS_LOGIN, "1")
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.account_created_to_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        authViewModel.getProfileData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            val payload = it.data.payload
                            Preferences.setStringPreference(
                                requireContext(),
                                EMAIL_ID1,
                                data?.user?.email
                            )
                            Preferences.setStringPreference(requireContext(), IS_LOGIN, "1")
                            Preferences.setCustomModelPreference(
                                requireContext(),
                                IS_PROFILE_DATA,
                                payload
                            )
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val cleanedPassword = password.replace(" ", "")
        return cleanedPassword.length >= 6
    }

    private fun isValidName(name: String): Boolean {
        return name.isNotEmpty()
    }
}