package com.app.adhyatmah.presentation.ui.login

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentForgotBinding
import com.app.adhyatmah.domain.model.auth.ForgotPassRequest
import com.app.adhyatmah.presentation.ui.viewmodel.AuthViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue


class ForgotFragment : BaseFragment<FragmentForgotBinding>() {

    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun setLayout(): Int {
        return R.layout.fragment_forgot
    }

    override fun initView(savedInstanceState: Bundle?) {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp() // or navigate to LoginFragment
            }
        })

        setObserver()

        binding.loginBtn.setOnClickListener {
            forgetPass()

        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }



    private fun setObserver() {



        authViewModel.getForgotPassData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload
                            binding.etForgotEmail.setText("")
                            Toast.makeText(requireActivity(),it.data.message,Toast.LENGTH_SHORT).show()
//                            findNavController().navigateUp()
                        }
                        401 -> {

                            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
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

    private fun forgetPass() {


        val email = binding.etForgotEmail.text?.trim().toString()
        if (email.isEmpty()) {
            Toast.makeText(requireActivity(), "Email is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireActivity(), "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        val request = ForgotPassRequest(email)
        authViewModel.getForgotData(request)

    }


}