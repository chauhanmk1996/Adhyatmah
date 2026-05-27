package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.databinding.FragmentApplyCouponsBinding
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons_req.ApplyCouponsRequest
import com.app.adhyatmah.presentation.ui.adapter.CouponAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.BagViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class ApplyCouponsFragment : BaseFragment<FragmentApplyCouponsBinding>() {

    private val bagViewModel by activityViewModels<BagViewModel>()

    private lateinit var couponAdapter: CouponAdapter

    var token = ""
    var cartId = ""


    override fun setLayout(): Int {
        return R.layout.fragment_apply_coupons
    }

    override fun initView(savedInstanceState: Bundle?) {

        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        cartId = Preferences.getStringPreference(requireContext(), CART_ID).toString()

        setObserver()
        bagViewModel.getCouponsList()
        bindViews()


        couponAdapter =
            CouponAdapter(mutableListOf(), object : CouponAdapter.OnApplyCouponsChangeListener {

                override fun onApplyCouponsChanged(coupons: String) {
                    hitApplyCouponsAPI(coupons)
                }
            })
        binding.itemsCodeRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = couponAdapter
        }

        // Handle back button click
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun bindViews() {

        binding.apply {

            enterCodeTv.setOnClickListener {
                findNavController().navigate(R.id.action_applyCouponsFragment_to_filtersFragment)
            }

            applyCouponBtn.setOnClickListener {
                applyCoupons()
            }
        }

    }

    private fun applyCoupons() {
        val couponCode = binding.couponCodeBtn.getString()

        if (couponCode.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.please_enter_a_coupon_code), Toast.LENGTH_SHORT)
                .show()
            return
        }
        val request = ApplyCouponsRequest(
            cartId,
            couponCode
        )
        bagViewModel.applyCouponsData(request)

//        binding.couponTv.setText("")

    }

    private fun hitApplyCouponsAPI(coupons: String) {
        val request = ApplyCouponsRequest(
            cartId,
            coupons
        )
        bagViewModel.applyCouponsData(request)
    }


    private fun setObserver() {

        bagViewModel.getCouponsListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {

                            val cartListContainer = it.data.payload.coupons
                            couponAdapter.updateCoupons(cartListContainer)

                            Log.d("tt", "sds, $cartListContainer")
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

        bagViewModel.applyCouponsRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload
//                            var coupons = data.discountCodes?.get(0)?.code
                            var coupons = data.discountCodes?.get(0)
                            bagViewModel.getCartList(token)

                           /* if(data.discountCodes?.get(0)?.applicable == true){
                                Toast.makeText(requireActivity(),"${it.data.message}",Toast.LENGTH_SHORT).show()
                                var bundle = Bundle()
                                bundle.putString("TYPE", "1")
                                bundle.putString("VALUE", coupons)
                                findNavController().navigate(R.id.action_applyCouponsFragment_to_bagFragment, bundle)

                            }*/
                            if(data.discount?.code?.isNotEmpty() == true){
                                Toast.makeText(requireActivity(),"${it.data.message}",Toast.LENGTH_SHORT).show()
                                var bundle = Bundle()
                                bundle.putString("TYPE", "1")
                                bundle.putString("VALUE", coupons)
                                findNavController().navigate(R.id.action_applyCouponsFragment_to_bagFragment, bundle)

                            }
                            else{
                                Toast.makeText(requireActivity(),"Coupon is not valid",Toast.LENGTH_SHORT).show()
                            }
                           // Toast.makeText(requireActivity(), "${it.data.message}", Toast.LENGTH_SHORT).show()
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


