package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.EMAIL_ID1
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentConfirmOrderBinding
import com.app.adhyatmah.domain.create_order.creat_order_request.CreaterOrderRequest
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest.Variant
import com.app.adhyatmah.domain.model.bagitem.BagItem
import com.app.adhyatmah.payment.paymentIniRequest.PaymentIniRequest
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.BagAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.BagViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.PaymentViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.getValue


class ConfirmOrderFragment : BaseFragment<FragmentConfirmOrderBinding>() {

    private val bagViewModel by activityViewModels<BagViewModel>()
    private val paymentViewModel by activityViewModels<PaymentViewModel>()

    private lateinit var confirmOrderAdapter: BagAdapter
    var addressId = ""
    var cartId = ""
    var address=""
    var paymentMethod =""
    var token = ""
    var currency_ghs =""
    var amounts = ""
    var emailId=""

    override fun setLayout(): Int {
        return R.layout.fragment_confirm_order
    }

    override fun initView(savedInstanceState: Bundle?) {

        token = Preferences.getStringPreference(requireContext(),ACCESS_TOKEN).toString()
        emailId = Preferences.getStringPreference(requireContext(), EMAIL_ID1).toString()
        Log.d("TAG", "initdfdsView: $emailId")
        paymentMethod = arguments?.getString("paymentMethod").toString()
        addressId = arguments?.getString("addressId").toString()
        cartId = Preferences.getStringPreference(requireContext(),CART_ID).toString()
        address = arguments?.getString("add").toString()

        Log.d("TAG","GetDetails: $paymentMethod,\n $addressId,\n $cartId}")


        // Initialize adapter with an empty list
//         bagAdapter = BagAdapter(mutableListOf())
       /* binding.confirmOrderRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = bagAdapter
        }
*/
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }

        clickConfirm(addressId,cartId,token)
        setObserver()
        setupRecyclerView()
        bagViewModel.getCartList(token)


        // Fetch bag items (simulated or from API/ViewModel)
//      fetchBagItems()

    //        binding.viewAllCouponsTv.setOnClickListener {
//            findNavController().navigate(R.id.action_bagFragment_to_applyCouponsFragment)
//        }
    }


    private fun setupRecyclerView() {
        confirmOrderAdapter = BagAdapter(mutableListOf(), object : BagAdapter.OnQuantityChangeListener {

            override fun onQuantityChanged(productId: String, newQuantity: Int, isPlus: Boolean) {
                hitAddAddressAPI(productId, newQuantity)

            }

            override fun onBagEmpty() {
                binding.confirmOrderRecycler.visibility = View.INVISIBLE
                binding.emptyTextView.visibility = View.VISIBLE // Add this TextView in XML
            }

        })

        binding.confirmOrderRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = confirmOrderAdapter

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


    fun clickConfirm(addressId: String?, carId: String?, token: String?) {

        binding.continueBtn.setOnClickListener {

            if (paymentMethod == "Cash on Delivery") {
                var request = CreaterOrderRequest()
                request.addressId = addressId
                request.cartId = carId
                request.accessToken =token
                paymentViewModel.hipAPICreateCODOrder(request)

            }else if(paymentMethod == "Credit or Debit Card"){

                val bundle = Bundle()
                bundle.putString("addressId",addressId)
                bundle.putString("cartId",carId)
                bundle.putString("add",address)
                bundle.putString("amount",amounts)
                bundle.putString("currency",currency_ghs)

                val request = PaymentIniRequest()
                request.accessToken = token
                request.addressId = addressId
                request.cartId = carId
                request.currency = currency_ghs
                request.email = emailId
                paymentViewModel.createPayStackOrder(request)


//               findNavController().navigate(R.id.action_confirmOrderFragment_to_stripFragment, bundle)
              // findNavController().navigate(R.id.action_confirmOrderFragment_to_paymentGatewayFragment,)
             //  findNavController().navigate(R.id.action_confirmOrderFragment_to_razorpayFragment,)
            }
            else {
                Toast.makeText(requireContext(), "Selected method is not Cash on Delivery", Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun setObserver(){

       /* bagViewModel.getCartListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.cart.cost
                            var edges = it.data.payload.cart.lines.edges
                            amounts = data?.totalAmount?.amount?:""
                            currency_ghs = data?.totalAmount?.currencyCode?:""
                            Log.d("TAG", "setObservercurrency: $currency_ghs")
                            binding.userAddressTv.text = address
                            binding.gpayTv.text = paymentMethod
                            val currentDateTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                            binding.dateTv.text = currentDateTime


                            if(edges.isNullOrEmpty()){
//                                binding.noBaglayout.visibility = View.VISIBLE
                                binding.bagLayout.visibility = View.GONE
                            }else{
//                                binding.noBaglayout.visibility = View.GONE
                                binding.bagLayout.visibility = View.VISIBLE
                                binding.rsTv.text = data?.totalAmount?.currencyCode+" "+data?.totalAmount?.amount?:""
                                val cartListContainer = it.data.payload.cart.lines.edges
                                confirmOrderAdapter.updateBagItems(cartListContainer)
                            }

                            Log.d("tt","sds, $data")
                            Log.d("Tdkjd",data.toString())
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
*/

        bagViewModel.getCartListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data?.payload?.cart

                            if (data != null) {
                                val cost = data.cost // Access cost safely
                                val edges = data.lines?.edges
                                CART_COUNT = edges?.size ?: 0
                                (requireActivity() as MainActivity).updateBagBadge(CART_COUNT)
                                amounts = cost?.totalAmount?.amount ?: ""
                                currency_ghs = cost?.totalAmount?.currencyCode ?: ""
                                Log.d("TAG", "setObservercurrency: $currency_ghs")

                                binding.userAddressTv.text = address
                                binding.gpayTv.text = paymentMethod
                                val currentDateTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                                binding.dateTv.text = currentDateTime

                                if (edges.isNullOrEmpty()) {
                                    binding.bagLayout.visibility = View.GONE
                                } else {
                                    binding.bagLayout.visibility = View.VISIBLE
                                    binding.rsTv.text = "${cost?.totalAmount?.currencyCode} ${cost?.totalAmount?.amount}"
                                    confirmOrderAdapter.updateBagItems(edges)
                                }

                                Log.d("tt", "sds, $data")
                                Log.d("Tdkjd", data.toString())
                            } else {
                                // Handle the case where cart data is null
                                Log.e("TAG", "Cart data is null!")
                                // You can show an error or fallback UI here
                            }
                        }
                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                        404 -> {
                            // Handle not found error
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


        paymentViewModel.getCOD().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.order
                            Toast.makeText(requireContext(),it.data.message,Toast.LENGTH_SHORT).show()
                            var orderId = it.data.payload.order.id.toString()
                            val bundle = Bundle()
                            bundle.putString("order_id",orderId)
                            Log.d("Tag","InitViewfdfd $orderId")
                            findNavController().navigate(R.id.action_confirmOrderFragment_to_congratulationFragment, bundle)
//                            findNavController().navigate(R.id.action_paymentMethodFragment_to_confirmOrderFragment)
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


        paymentViewModel.getPayStackApisResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload

                            /*val bundle = Bundle()
                            bundle.putString("authUrl",data.authorization_url)
                            bundle.putString("link",data.payment_link_id)
                            bundle.putString("order_id",data.order_id)
                            Log.d("Tag","InitViewdfd ${data.authorization_url}")
                            Log.d("Tag","InisdfstViewdfd ${data}")
                            findNavController().navigate(R.id.action_confirmOrderFragment_to_paymentGatewayFragment, bundle)*/

                            val razor = it.data.payload.razorpay

                            val bundle = Bundle().apply {
                                putString("paymentUrl", razor.payment_link.short_url)
                                putString("successUrl", data.success_url_app)   // backend will provide
                                putString("order_id", razor.order_id)
                            }

                            findNavController().navigate(
                                R.id.action_confirmOrderFragment_to_paymentGatewayFragment,
                                bundle
                            )
                            Toast.makeText(requireContext(),it.data.message,Toast.LENGTH_SHORT).show()
//                          findNavController().navigate(R.id.action_paymentMethodFragment_to_congratulationFragment)
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





    private fun fetchBagItems() {
        // Simulated API call (replace with actual API/ViewModel call)
        val bagItems = mutableListOf(
            BagItem(R.drawable.model_image2, "Grey Pullover", "₹799", "XS", "Grey", 1),
            BagItem(R.drawable.model_image2, "Grey Pullover", "₹799", "XS", "Grey", 1),
        )

    }
}