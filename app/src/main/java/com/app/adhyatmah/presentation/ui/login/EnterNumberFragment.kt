package com.app.adhyatmah.presentation.ui.login

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentEnterNumberBinding
import com.app.adhyatmah.domain.model.send_otp.LoginWithMobileRequest
import com.app.adhyatmah.presentation.ui.viewmodel.AuthViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.google.gson.Gson

class EnterNumberFragment : BaseFragment<FragmentEnterNumberBinding>() {
    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun setLayout(): Int {
        return R.layout.fragment_enter_number
    }

    override fun initView(savedInstanceState: Bundle?) {
        initViews()
        setCountryPicker()
    }

    private fun setCountryPicker() {
        binding.countryPicker.apply {
            setCountryForNameCode("IN")
            isClickable = false
            isEnabled = false
        }
        binding.countryPicker.setOnClickListener(null)
        binding.countryPicker.setCcpClickable(false)

    }

    private fun initViews() {
        alreadyHaveAccount()
        binding.loginBtn.setOnClickListener {
            if (validation()) {
                sendOtp()
            }
        }
        binding.loginPage.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
    }

    private fun validation(): Boolean {
        if (binding.phonenumberInput.getString().isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.please_enter_the_number), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.phonenumberInput.getString().length < 10) {
            Toast.makeText(requireContext(),
                getString(R.string.please_enter_a_valid_number), Toast.LENGTH_SHORT)
                .show()
            return false
        } else {
            return true
        }
    }

    private fun alreadyHaveAccount() {
        val fullText = getString(R.string.don_t_have_an_account_sign_up)
        val spannableString = SpannableString(fullText)
        val signUpClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().popBackStack()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorPrimary
                )
                ds.isUnderlineText = true
            }
        }

        val signUpStart = fullText.indexOf(getString(R.string.sign_up))
        val signUpEnd = signUpStart + getString(R.string.sign_up).length

        spannableString.setSpan(
            signUpClick,
            signUpStart,
            signUpEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.alreadyHaveAccountId.text = spannableString
        binding.alreadyHaveAccountId.movementMethod = LinkMovementMethod.getInstance()
        binding.alreadyHaveAccountId.highlightColor = Color.TRANSPARENT
    }

    private fun sendOtp() {
        val request = LoginWithMobileRequest(mobile = binding.phonenumberInput.text.toString())
        authViewModel.hitLoginWithMobileData(request)
        authViewModel.getLoginWithMobileData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.code == 200) {
                        UserPreference.MOBILE_NUMBER = binding.phonenumberInput.getString()
                        findNavController().navigate(R.id.otpFragment)
                    } else if (it.data?.message == "Account not active.") {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.your_account_is_not_active_redirecting_to_registration),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(requireActivity(), it.data?.message, Toast.LENGTH_LONG)
                            .show()
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
    }
}