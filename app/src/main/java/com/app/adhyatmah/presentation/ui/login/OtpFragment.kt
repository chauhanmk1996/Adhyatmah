package com.app.adhyatmah.presentation.ui.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.EMAIL_ID
import com.app.adhyatmah.data.preferences.EMAIL_ID1
import com.app.adhyatmah.data.preferences.FCM_TOKEN
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.IS_PROFILE_DATA
import com.app.adhyatmah.data.preferences.PROFILE_IMG
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentOtpBinding
import com.app.adhyatmah.domain.model.registration.RegistrationModel
import com.app.adhyatmah.domain.model.send_otp.SendOtpModel
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.viewmodel.AuthViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.gson.Gson
import kotlin.getValue

class OtpFragment : BaseFragment<FragmentOtpBinding>() {
    private var progresbar: Dialog? = null
    private var countdown: CountDownTimer?=null
    private var isTimerRunning = true
    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun setLayout(): Int {

        return R.layout.fragment_otp
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.ivBack.setOnClickListener {
            it.findNavController().popBackStack()
        }
//        AppUtils.setupHideKeyboardOnTouch(binding.root,requireActivity())
        initViews()
    }

    private fun initViews() {
        startTimer()

        binding.pinview.setPinViewEventListener { pinView, fromUser ->
            val otp = pinView.value
            if (otp.length == 4) {
                hideKeyboard(binding.pinview)
            }
        }

        binding.btnVerify.setOnClickListener {
            validation()
        }
    }

    private fun validation() {
        val otp = binding.pinview.value

        if(otp.isEmpty()){
            Toast.makeText(requireContext(),
                getString(R.string.please_enter_otp), Toast.LENGTH_SHORT).show()
        }else{
            val model = RegistrationModel(
                mobile = UserPreference.MOBILE_NUMBER,
                otp = otp,
                deviceType = "android",
                deviceToken = Preferences.getStringPreference(requireContext(), FCM_TOKEN),
//              user_type = ""
            )
            verifyOtp(model)
        }
    }
    private fun startTimer() {
        // Cancel existing timer if any
        countdown?.cancel()

        countdown = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                var t1 = ""
                var t2: String
                val min = seconds / 60
                val sec = seconds % 60
                t2 = if (sec < 10) {
                    "0${sec}"
                } else {
                    "$sec"
                }
                if (min < 10) {
                    t1 = "0${min}"
                } else {
                    t1 = "$min"
                }

                binding.tvTimer.text = "${getString(R.string.resend_confirmation_code)} ($t1:$t2)"
                binding.tvResend.isEnabled = false
                context?.let {
                    binding.tvResend.setTextColor(ContextCompat.getColor(it, R.color.gray))
                }
            }

            override fun onFinish() {
                if (isAdded){
                    isTimerRunning = false
                    binding.tvTimer.text = "00:00"
                    binding.tvResend.isEnabled = true
                    context?.let {
                        binding.tvResend.setTextColor(ContextCompat.getColor(it, R.color.blue))
                    }

                    binding.tvResend.setOnClickListener {
                        // Prevent multiple requests while timer is running
                        if (!isTimerRunning) {
                            val model = SendOtpModel(mobile = UserPreference.MOBILE_NUMBER)
                            reSendOtp(model)
                        }
                    }
                }
            }
        }.start()
    }
    private var isResendInProgress = false

    private fun reSendOtp(model: SendOtpModel) {
        if (isResendInProgress) return  // Prevent multiple requests
        isTimerRunning=true
        isResendInProgress = true
        authViewModel.hitResendOtpData(model)
        authViewModel.getResendOtpData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "resend success: ${Gson().toJson(it)}")
                    isResendInProgress = false
                    Toast.makeText(requireActivity(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    startTimer()  // Restart timer after OTP is sent
                    ProcessDialog.dismissDialog(true)
                }
                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(),it.data?.message?:"",Toast.LENGTH_SHORT).show()
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    private fun verifyOtp(model: RegistrationModel) {
        authViewModel.hitVerifyOtpData(model)
        authViewModel.getVerifyOtpData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    val data = it.data?.payload
                    if (data != null) {
                        var data = it.data.payload
                        var customId = it.data.payload?.customer?.id ?: ""
//                        authViewModel.hitAPIProfileImageData(customId)
                        if (data.role=="user"){
                            Preferences.setStringPreference(requireContext(), IS_LOGIN, "1")
                            Preferences.setStringPreference(requireContext(), ACCESS_TOKEN, data.accessToken)
                            Preferences.setStringPreference(requireContext(), PROFILE_IMG, it.data.payload.customer.cover?.url)
                            Preferences.setCustomModelPreference(requireContext(), IS_PROFILE_DATA, data)
                            Preferences.setStringPreference(requireContext(), EMAIL_ID, data?.customer?.id)
                            Preferences.setStringPreference(requireContext(), EMAIL_ID1, data?.customer?.email)
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                        }else{
                            Toast.makeText(requireContext(),
                                getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show()
                        }

                    }
                    ProcessDialog.dismissDialog(true)
                }
                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(),it.data?.message?:"",Toast.LENGTH_SHORT).show()
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}