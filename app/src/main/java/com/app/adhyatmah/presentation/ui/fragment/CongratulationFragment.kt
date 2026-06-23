package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.FragmentCongratulationBinding
import com.app.adhyatmah.payment.payment_clear_order_request.PaymentSuccessClearOrderRequest
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.viewmodel.PaymentViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class CongratulationFragment : BaseFragment<FragmentCongratulationBinding>() {

    private val paymentViewModel by activityViewModels<PaymentViewModel>()
    var token = ""
    var orderId = ""

    override fun setLayout(): Int {
        return R.layout.fragment_congratulation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    override fun initView(savedInstanceState: Bundle?) {
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        val req = PaymentSuccessClearOrderRequest(token)
        paymentViewModel.paymentSuOrderClearAPIs(req)
        setObserver()
    }

    private fun setObserver() {
        paymentViewModel.getPaymentSuOrderClearRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            orderId = arguments?.getString("order_id").toString()
                            val orderIdText = "${getString(R.string.order_id_heading)} $orderId"
                            binding.orderID.text = orderIdText
                            binding.btn.setOnClickListener {
                                findNavController().navigate(R.id.action_congratulationFragment_to_homeFragment)
                                (requireActivity() as MainActivity).setBottomNavSelected(R.id.navigation_home)
                            }
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
}