package com.app.adhyatmah.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.EMAIL_ID
import com.app.adhyatmah.data.preferences.EMAIL_ID1
import com.app.adhyatmah.data.preferences.FCM_TOKEN
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.IS_PROFILE_DATA
import com.app.adhyatmah.data.preferences.PROFILE_IMG
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentLoginBinding
import com.app.adhyatmah.domain.model.auth.LoginRequest
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.viewmodel.AuthViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.google.gson.Gson

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val authViewModel by activityViewModels<AuthViewModel>()
    private var isPasswordVisible = false

    override fun setLayout(): Int {
        return R.layout.fragment_login
    }

    override fun initView(savedInstanceState: Bundle?) {
        onClick()
        setObserver()

        binding.fogotPass.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
        }

        binding.togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.passInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_off) // 👁️ eye-open icon
            } else {
                binding.passInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_on) // 🙈 eye-off icon
            }
            binding.passInput.setSelection(binding.passInput.text!!.length) // keep cursor at end
        }
    }


    fun onClick() {
        binding.loginBtnWithOtp.setOnClickListener {
            findNavController().navigate(R.id.enterNumberFragment)
        }

        binding.loginPage.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.phonenumberInput.getString()
            val password = binding.passInput.getString()

            if (email.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_your_email), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (!isValidEmail(email)) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.invalid_email_format), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_your_password), Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            } else if (!isValidPassword(password)) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.password_must_be_at_least_6_characters),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val fcmToken = Preferences.getStringPreference(requireContext(), FCM_TOKEN)
            val request =
                LoginRequest(email, password, deviceType = "android", deviceToken = fcmToken)
            authViewModel.getLoginData(request)
        }
    }

    private fun setObserver() {
        authViewModel.getLoginData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == 1) {
                        if (it.data.code == 200) {
                            if (it.data.payload.isUser) {
                                val token = it.data.payload.accessToken
                                Log.d("TAG", "setObserver: $token")
                                Preferences.setStringPreference(requireContext(), IS_LOGIN, "1")
                                Preferences.setStringPreference(
                                    requireContext(),
                                    ACCESS_TOKEN,
                                    token
                                )
                                authViewModel.hitAPIProfileData(token)
                                Toast.makeText(
                                    requireContext(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.invalid_credentials),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                it.data.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "${it.data?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.data?.message ?: "", Toast.LENGTH_SHORT)
                        .show()
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
        authViewModel.getProfileData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val msg = it.data?.message ?: getString(R.string.something_went_wrong)

                    if (it.data?.status == 1) {
                        if (it.data.code == 200) {
                            val data = it.data.payload
                            val customId = it.data.payload?.user?.id ?: ""
                            authViewModel.hitAPIProfileImageData(customId)
                            Preferences.setCustomModelPreference(
                                requireContext(),
                                IS_PROFILE_DATA,
                                data
                            )
                            Preferences.setStringPreference(
                                requireContext(),
                                EMAIL_ID,
                                data?.user?.id
                            )
                            Preferences.setStringPreference(
                                requireContext(),
                                EMAIL_ID1,
                                data?.user?.email
                            )
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
        authViewModel.getProfileImgData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.code == 200) {
                        Preferences.setStringPreference(
                            requireContext(),
                            PROFILE_IMG,
                            it.data.payload.url
                        )
                    } else if (it.data?.code == 404) {
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()

                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}