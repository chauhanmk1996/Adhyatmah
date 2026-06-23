package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.ADDRESS_ID
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentBagBinding
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons_req.ApplyCouponsRequest
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest.Variant
import com.app.adhyatmah.domain.model.bag_response.remove_coupon.remove_coupon_request.RemoveCouponRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.BagAdapter
import com.app.adhyatmah.presentation.ui.bottom_sheet.SignUpRequiredBottomSheetFragment
import com.app.adhyatmah.presentation.ui.viewmodel.BagViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class BagFragment : BaseFragment<FragmentBagBinding>() {

    private val bagViewModel by activityViewModels<BagViewModel>()
    private lateinit var bagAdapter: BagAdapter
    private var isApply: Boolean = false
    var token = ""
    var cartId = ""
    var isItemPlus = false

    override fun setLayout(): Int {
        return R.layout.fragment_bag
    }

    override fun initView(savedInstanceState: Bundle?) {
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        cartId = Preferences.getStringPreference(requireContext(), CART_ID).toString()
        val type = arguments?.getString("TYPE")
        val value = arguments?.getString("VALUE")

        if (type == "1") {
            val request = RemoveCouponRequest()
            request.cartId = cartId
            binding.removeCoupon.visibility = View.GONE
            binding.cancelText.text = value
            binding.couponTv.setText(value)
            binding.applyTv.text = getString(R.string.remove)
            binding.cancelCouponBtn.setOnClickListener {
                bagViewModel.hitRemoveCouponAPI(request)
            }
        } else {
            binding.applyTv.text = getString(R.string.apply)
            binding.removeCoupon.visibility = View.GONE
            binding.applyTv.setOnClickListener {
                applyCoupons(type)
            }
        }

        initializeView(type)
        setObserver()
        setupRecyclerView()

        if (token.isEmpty()) {
            signupRequired(getString(R.string.please_sign_up_to_add_items_to_your_wishlist))
        } else {
            binding.bagLayout.visibility = View.VISIBLE
            binding.noBaglayout.visibility = View.GONE
            bagViewModel.getCartList(token)
        }
    }

    override fun onResume() {
        super.onResume()
        if (UserPreference.savedAddress.isEmpty()) {
            binding.btnSelectAddress.show()
            binding.tvSelectedAddress.hide()
            binding.tvChange.hide()
        } else {
            binding.btnSelectAddress.hide()
            binding.tvSelectedAddress.show()
            binding.tvChange.show()
            binding.tvSelectedAddress.text = UserPreference.savedAddress
        }
    }

    private fun initializeView(type: String?) {
        binding.apply {
            checkoutBtn.setOnClickListener {
                val bundle = Bundle().apply {
                    putString(CART_ID, cartId)
                    putString(ADDRESS_ID, UserPreference.savedAddressId)
                }
                findNavController().navigate(R.id.paymentMethodFragment, bundle)
            }

            viewAllCouponsTv.setOnClickListener {
                findNavController().navigate(R.id.action_bagFragment_to_applyCouponsFragment)
            }

            binding.applyTv.setOnClickListener {
                applyCoupons(type)
            }

            binding.btnSelectAddress.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "bookPanditji")
                }
                findNavController().navigate(R.id.mangeAddressFragment, bundle)
            }

            binding.tvChange.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "bookPanditji")
                }
                findNavController().navigate(R.id.mangeAddressFragment, bundle)
            }
        }
    }

    private fun applyCoupons(type: String?) {
        val couponCode = binding.couponTv.getString()
        if (couponCode.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_enter_a_coupon_code),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request = ApplyCouponsRequest(cartId, couponCode)
        if (type == "1") {
            removeCoupon()
        } else {
            if (isApply) {
                removeCoupon()
            } else {
                bagViewModel.applyCouponsData(request)

            }
        }

        binding.applyTv.text = getString(R.string.remove)

        binding.cancelCouponBtn.setOnClickListener {
            removeCoupon()
        }
        binding.couponTv.setText(couponCode)
    }

    private fun signupRequired(message: String) {
        val bottomSheet =
            SignUpRequiredBottomSheetFragment(message) {
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                requireActivity().startActivity(intent)
            }
        bottomSheet.show(parentFragmentManager, "SignUpRequiredBottomSheetFragment")
    }

    private fun setupRecyclerView() {
        bagAdapter = BagAdapter(mutableListOf(), object : BagAdapter.OnQuantityChangeListener {

            override fun onQuantityChanged(productId: String, newQuantity: Int, isPlus: Boolean) {
                hitAddAddressAPI(productId, newQuantity)
                isItemPlus = isPlus
            }

            override fun onBagEmpty() {
                binding.itemsBagRecycler.visibility = View.INVISIBLE
                binding.emptyTextView.visibility = View.VISIBLE // Add this TextView in XML
            }
        })

        binding.itemsBagRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = bagAdapter
        }
    }

    private fun hitAddAddressAPI(productVariantId: String, quantity: Int) {
        val request = IncreaseQtyRequest().apply {
            accessToken = token
            variant = Variant().apply {
                id = productVariantId
                this.quantity = quantity
            }
        }
        bagViewModel.getPlusQtyList(request)
    }

    private fun removeCoupon() {
        val request = RemoveCouponRequest()
        request.cartId = cartId
        bagViewModel.hitRemoveCouponAPI(request)
        binding.applyTv.text = getString(R.string.apply)
        binding.couponTv.setText("")
        binding.removeCoupon.visibility = View.GONE
    }

    private fun setObserver() {
        bagViewModel.getCartListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val payload = it.data.payload
                            CART_COUNT = payload.cart?.lines?.edges?.size ?: 0
                            (requireActivity() as MainActivity).updateBagBadge(CART_COUNT)
                            cartId = Preferences.setStringPreference(
                                requireContext(),
                                CART_ID,
                                it.data.payload.cart?.id
                            ).toString()

                            if (payload.cart == null || payload.cart.lines == null || payload.cart.lines.edges.isNullOrEmpty()) {
                                Log.d("TAG", "setObserver1: payload is null")
                                binding.noBaglayout.visibility = View.VISIBLE
                                binding.bagLayout.visibility = View.GONE
                                binding.checkoutBtn.visibility = View.GONE
                            } else {
                                Log.d("TAG", "setObserver1: payload is not null")
                                val data = it.data.payload.cart.cost
                                val totalAmount = it.data.payload.discountInfo?.savingsSummary
                                val total =
                                    totalAmount?.currencyCode + " " + totalAmount?.finalTotal
                                val subtotal =
                                    totalAmount?.currencyCode + " " + totalAmount?.originalTotal
                                val edges = it.data.payload.cart.lines.edges
                                val tax =
                                    data?.totalTaxAmount?.currencyCode + " " + data?.totalTaxAmount?.amount
                                val shippingFee =
                                    totalAmount?.currencyCode + " " + it.data.payload.cart.shipping_fee
                                val platformFee =
                                    totalAmount?.currencyCode + " " + it.data.payload.cart.platform_fee
                                val disInfo = it.data.payload.discountInfo
                                val isDiscount = disInfo?.hasDiscounts

                                if (disInfo != null) {
                                    val discount = "${data?.totalAmount?.currencyCode} ${
                                        String.format(
                                            Locale.getDefault(),
                                            "%.2f",
                                            disInfo.totalDiscount
                                        )
                                    }"
                                    val discountPriceTvText = "- $discount"
                                    binding.discountPriceTv.text = discountPriceTvText
                                    if (isDiscount == true) {
                                        val coupon = it.data.payload.cart.discountCodes?.get(0)
                                        isApply = true
                                        binding.couponTv.setText(coupon)
                                        binding.applyTv.text = getString(R.string.remove)
                                        binding.removeCoupon.visibility = View.GONE
                                        binding.cancelText.text = coupon
                                        binding.cancelCouponBtn.setOnClickListener {
                                            removeCoupon()
                                        }
                                        binding.couponTv.isClickable = false
                                        binding.couponTv.isEnabled = false

                                    } else {
                                        isApply = false
                                        binding.couponTv.isEnabled = true
                                        binding.couponTv.isClickable = true
                                        binding.applyTv.text = getString(R.string.apply)
                                        binding.removeCoupon.visibility = View.GONE
                                        binding.couponTv.setText("")
                                        binding.applyTv.setOnClickListener {
                                            val type = arguments?.getString("TYPE")
                                            applyCoupons(type)
                                        }
                                    }
                                } else {
                                    binding.discountPriceTv.text =
                                        getString(R.string.no_discount_applied)
                                }

                                if (edges.isEmpty() && data == null) {
                                    binding.noBaglayout.visibility = View.VISIBLE
                                    binding.bagLayout.visibility = View.GONE
                                    binding.checkoutBtn.visibility = View.GONE
                                } else {
                                    binding.noBaglayout.visibility = View.GONE
                                    binding.bagLayout.visibility = View.VISIBLE
                                    binding.checkoutBtn.visibility = View.VISIBLE
                                    binding.discountTv.text = tax
                                    binding.shippinpPriceTv.text = shippingFee
                                    binding.platformPriceTv.text = platformFee
                                    binding.totalAmountPrice.text = total
                                    binding.priceTv.text = subtotal
                                    val cartListContainer = it.data.payload.cart.lines.edges
                                    bagAdapter.updateBagItems(cartListContainer)
                                }
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

        bagViewModel.getPlusQtyData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.cart
                            bagViewModel.getCartList(token)
                            Log.d("tt", "sds, $data")
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
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            val discountCode = data.discountCodes?.get(0)
                            val isApplicable = data.discountCodes?.isNotEmpty()

                            if (isApplicable == true) {
                                isApply = true
                                bagViewModel.getCartList(token)
                                binding.applyTv.text = getString(R.string.remove)
                                binding.couponTv.setText(discountCode)
                                binding.removeCoupon.visibility = View.GONE
                                binding.cancelText.text = discountCode
                                Toast.makeText(
                                    requireActivity(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.cancelCouponBtn.setOnClickListener {
                                    removeCoupon()
                                }
                            } else {
                                isApply = false
                                Toast.makeText(
                                    requireActivity(),
                                    getString(R.string.coupon_is_not_valid), Toast.LENGTH_SHORT
                                ).show()
                                binding.removeCoupon.visibility = View.GONE
                                binding.couponTv.setText("")
                                binding.applyTv.text = getString(R.string.apply)
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
                    ProcessDialog.dismissDialog(true)
                    Log.e("TAG", "Error: ${it.message}")
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        bagViewModel.getRemoveCouponData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            bagViewModel.getCartList(token)
                            isApply = false
                            binding.applyTv.text = getString(R.string.apply)
                            binding.couponTv.setText("")
                            binding.removeCoupon.visibility = View.GONE
                            Toast.makeText(requireActivity(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
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
                    ProcessDialog.dismissDialog(true)
                    Log.e("TAG", "Error: ${it.message}")
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}