package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentBagBinding
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_response.Edge
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons_req.ApplyCouponsRequest
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest.Variant
import com.app.adhyatmah.domain.model.bag_response.remove_coupon.remove_coupon_request.RemoveCouponRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.BagAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.BagViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.google.android.material.snackbar.Snackbar

class BagFragment : BaseFragment<FragmentBagBinding>() {

    private val bagViewModel by activityViewModels<BagViewModel>()
    val cartList = mutableListOf<Edge>()

    private lateinit var bagAdapter: BagAdapter
    private var isApply :Boolean = false

    private var count = 1  // Default value

    var token = ""
    var cartId = ""
    var isItemPlus = false

    override fun setLayout(): Int {
        return R.layout.fragment_bag
    }

    override fun initView(savedInstanceState: Bundle?) {

        token = Preferences.getStringPreference(requireContext(),ACCESS_TOKEN).toString()
        cartId = Preferences.getStringPreference(requireContext(),CART_ID).toString()
        Log.d("TAG", "inasdasitView: $cartId")


      var type = arguments?.getString("TYPE")
        var value = arguments?.getString("VALUE")
        Log.d("TAG", "removeCoupon: $type $value")
        if(type=="1"){
            var request = RemoveCouponRequest()
//            request.discountCode  = value
            request.cartId = cartId
            binding.removeCoupon.visibility = View.GONE
            binding.cancelText.text = value
            binding.couponTv.setText(value)
            binding.applyTv.setText("Remove")
            binding.cancelCouponBtn.setOnClickListener {
                bagViewModel.hitRemoveCouponAPI(request)
            }

        }else{
            binding.applyTv.setText("Apply")
            binding.removeCoupon.visibility = View.GONE
            binding.applyTv.setOnClickListener {
                applyCoupons(type)
            }

        }

        initializeView(type)
        setObserver()
        setupRecyclerView()

        if(token.isNullOrEmpty()){
            showLoginPrompt()
        }
        /*else if (cartId.isNullOrEmpty()){
            binding.noBaglayout.visibility = View.VISIBLE
            binding.bagLayout.visibility = View.GONE
        }*/
        else{
            binding.bagLayout.visibility = View.VISIBLE
            binding.noBaglayout.visibility = View.GONE
            bagViewModel.getCartList(token)
        }


    }


    private fun initializeView(type: String?) {

        binding.apply {

            checkoutBtn.setOnClickListener {
                var bundle = Bundle()
                bundle.putString(CART_ID,cartId)
                findNavController().navigate(R.id.action_bagFragment_to_mangeAddressFragment,bundle)
//                Toast.makeText(requireContext(), "under development", Toast.LENGTH_SHORT).show()
            }
            viewAllCouponsTv.setOnClickListener {
//                Toast.makeText(requireContext(), "under development", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_bagFragment_to_applyCouponsFragment)
            }

            binding.applyTv.setOnClickListener {
                applyCoupons(type)
            }

        }
    }



   private fun applyCoupons(type: String?) {
       val couponCode = binding.couponTv.getString()

       if (couponCode.isEmpty()) {
           Toast.makeText(requireContext(), getString(R.string.please_enter_a_coupon_code), Toast.LENGTH_SHORT).show()
           return
       }

       val request = ApplyCouponsRequest(
           cartId,
           couponCode
       )
        if(type=="1"){
            removeCoupon(couponCode)
        }else{
            if (isApply){
                removeCoupon(couponCode)
            }else{
                bagViewModel.applyCouponsData(request)

            }
        }
       // Apply the coupon

       // Change UI after applying the coupon
       binding.applyTv.text = "Remove" // Change button text to "Remove Coupon"
       binding.cancelCouponBtn.setOnClickListener {
           // Remove the coupon when clicked
           removeCoupon(couponCode)
       }
       binding.couponTv.setText(couponCode) // Show the applied coupon code
   }



    private fun showLoginPrompt() {
        var dialog: AlertDialog? = null
        dialog=  CommonUtils.showCustomAlertDialog(
            requireActivity() ,
            getString(R.string.sign_up_required),
            getString(R.string.please_sign_up_to_add_items_to_your_wishlist),
            positiveButtonText = getString(R.string.sign_up),
            negativeButtonText = getString(R.string.cancel),
            positiveButtonAction = {
                dialog?.dismiss()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                val bundle = Bundle()
                bundle.putString("previousScreen", "logout")
                bundle.putString("selectedImage", "0")
                intent.putExtras(bundle)
                requireActivity().startActivity(intent)            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
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
            accessToken = token // avoid hardcoding in production
            variant = Variant().apply {
                id = productVariantId
                this.quantity = quantity

            }
        }
        bagViewModel.getPlusQtyList(request)
    }
    private fun removeCoupon(coupon: String) {
        val request = RemoveCouponRequest()
//        request.discountCode = coupon
        request.cartId = cartId

        // Call API to remove coupon
        bagViewModel.hitRemoveCouponAPI(request)

        // Update UI after removing the coupon
        binding.applyTv.text = getString(R.string.apply) // Change button text back to "Apply Coupon"
        binding.couponTv.setText("") // Clear the coupon code text
        binding.removeCoupon.visibility = View.GONE // Hide the remove coupon UI component
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver(){

        bagViewModel.getCartListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val payload = it.data.payload
                            CART_COUNT = payload.cart?.lines?.edges?.size ?: 0
                            (requireActivity() as MainActivity).updateBagBadge(CART_COUNT)
                            cartId = Preferences.setStringPreference(requireContext(),CART_ID, it.data.payload.cart?.id).toString()

                            if(it.data.payload==null||payload.cart == null || payload.cart.lines == null || payload.cart.lines.edges.isNullOrEmpty()){
                                Log.d("TAG", "setObserver1: payload is null")
                                binding.noBaglayout.visibility = View.VISIBLE
                                binding.bagLayout.visibility = View.GONE
                                binding.checkoutBtn.visibility = View.GONE
                            }
                            else{
                                Log.d("TAG", "setObserver1: payload is not null")
                                var data = it.data.payload.cart.cost
                                var totalAmount = it.data.payload.discountInfo?.savingsSummary
                                var total = totalAmount?.currencyCode+" "+totalAmount?.finalTotal
                                var subtotal =totalAmount?.currencyCode+" "+totalAmount?.originalTotal
                                var edges = it.data.payload.cart.lines.edges
                                var tax = data?.totalTaxAmount?.currencyCode+" "+data?.totalTaxAmount?.amount
                                var shippingFee = totalAmount?.currencyCode+" "+it.data.payload.cart.shipping_fee
                                var platformFee = totalAmount?.currencyCode+" "+it.data.payload.cart.platform_fee
/*
                                if (disc.isNullOrEmpty()){
                                    binding.discountPriceTv.text = ""
                                }
                                else{
                             //   binding.discountPriceTv.text = disc[0].
                             //   binding.discountPriceTv.text =
                                }*/
                                val disInfo = it.data.payload.discountInfo
                                var isDiscount = disInfo?.hasDiscounts

                                if (disInfo != null) {
                                    var discount = "${data?.totalAmount?.currencyCode} ${String.format("%.2f", disInfo.totalDiscount?.toDouble())}"
                                    binding.discountPriceTv.text = "- "+discount
                                    if(isDiscount == true){
//                                        var coupon =  it.data.payload.cart.discountCodes?.get(0)?.code
                                        var coupon =  it.data.payload.cart.discountCodes?.get(0)
                                        isApply = true
                                        binding.couponTv.setText(coupon)
                                        binding.applyTv.text = "Remove"
                                        binding.removeCoupon.visibility = View.GONE
                                        binding.cancelText.setText(coupon)
                                        binding.cancelCouponBtn.setOnClickListener {
                                            removeCoupon(discount)
                                        }
                                        binding.couponTv.isClickable=false
                                        binding.couponTv.isEnabled = false

                                    }else{
                                        isApply = false
                                        binding.couponTv.isEnabled = true
                                        binding.couponTv.isClickable=true
                                        binding.applyTv.text = getString(R.string.apply)
                                        binding.removeCoupon.visibility = View.GONE
                                        binding.couponTv.setText("")
                                        binding.applyTv.setOnClickListener {
                                            var type = arguments?.getString("TYPE")
                                            applyCoupons(type)
                                        }

                                    }

                                } else {
                                    binding.discountPriceTv.text =
                                        getString(R.string.no_discount_applied)
                                }


                                if(edges.isNullOrEmpty()&& data==null){
                                    binding.noBaglayout.visibility = View.VISIBLE
                                    binding.bagLayout.visibility = View.GONE
                                    binding.checkoutBtn.visibility = View.GONE
                                }else{
                                    binding.noBaglayout.visibility = View.GONE
                                    binding.bagLayout.visibility = View.VISIBLE
                                    binding.checkoutBtn.visibility = View.VISIBLE
                                    binding.discountTv.text = tax
                                    binding.shippinpPriceTv.text = shippingFee.toString()
                                    binding.platformPriceTv.text = platformFee.toString()
                                    binding.totalAmountPrice.text = total//data.totalAmount.currencyCode+" "+data.totalAmount.amount
                                    binding.priceTv.text =subtotal //data.totalAmount.currencyCode+" "+data.totalAmount.amount
                                    val cartListContainer = it.data.payload.cart.lines.edges
                                    bagAdapter.updateBagItems(cartListContainer)
                                }

                            }

//                          setAdapter(data)
                            // setup adapter here
                        }
                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                        404->{
                            //    Toast.makeText(requireContext(),)
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
                            var data = it.data.payload.cart

                            bagViewModel.getCartList(token)
                            /*if (isItemPlus){
                                CART_COUNT = CART_COUNT+1
                                (requireActivity() as MainActivity).updateBagBadge(CART_COUNT)
                            }else{
                                if (CART_COUNT>0) CART_COUNT = CART_COUNT-1
                                (requireActivity() as MainActivity).updateBagBadge(CART_COUNT)
                            }*/
//                            Toast.makeText(requireActivity(),"${it.data.message}",Toast.LENGTH_SHORT).show()
                            Log.d("tt","sds, $data")
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
//                            val discountCode = data.discountCodes?.get(0)?.code
//                            val isApplicable = data.discountCodes?.get(0)?.applicable

                            val discountCode = data.discountCodes?.get(0)
                            val isApplicable = data.discountCodes?.isNotEmpty()

                            if (isApplicable == true) {
                                isApply = true
                                bagViewModel.getCartList(token) // Refresh the cart
                                // Update UI to show applied coupon
                                binding.applyTv.text = getString(R.string.remove)
                                binding.couponTv.setText(discountCode)
                                binding.removeCoupon.visibility = View.GONE
                                binding.cancelText.text = discountCode
                                Toast.makeText(requireActivity(), "${it.data.message}", Toast.LENGTH_SHORT).show()

                                // Optional: handle remove logic in one place
                                binding.cancelCouponBtn.setOnClickListener {
                                    removeCoupon(discountCode ?: "")
                                }

                            } else {
                                // If coupon is not applicable
                                isApply = false
                                Toast.makeText(requireActivity(),
                                    getString(R.string.coupon_is_not_valid), Toast.LENGTH_SHORT).show()
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
                            // Update UI for successful coupon removal
                            bagViewModel.getCartList(token)
                            isApply =false
                            binding.applyTv.text = getString(R.string.apply) // Change button back to "Apply Coupon"
                            binding.couponTv.setText("") // Clear the coupon text
                            binding.removeCoupon.visibility = View.GONE // Hide remove coupon UI
                            Toast.makeText(requireActivity(), "${it.data.message}", Toast.LENGTH_SHORT).show()
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