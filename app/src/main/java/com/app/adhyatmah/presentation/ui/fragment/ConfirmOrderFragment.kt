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
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentConfirmOrderBinding
import com.app.adhyatmah.domain.create_order.creat_order_request.CreaterOrderRequest
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest.Variant
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
    var cartId = ""
    var paymentMethod = ""
    var token = ""
    var currency = ""
    var amounts = ""
    var emailId = ""

    override fun setLayout(): Int {
        return R.layout.fragment_confirm_order
    }

    override fun initView(savedInstanceState: Bundle?) {
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        emailId = Preferences.getStringPreference(requireContext(), EMAIL_ID1).toString()
        paymentMethod = arguments?.getString("paymentMethod").toString()
        cartId = Preferences.getStringPreference(requireContext(), CART_ID).toString()

        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }

        clickConfirm(UserPreference.savedAddressId, cartId, token)
        setObserver()
        setupRecyclerView()
        bagViewModel.getCartList(token)
    }

    private fun setupRecyclerView() {
        confirmOrderAdapter =
            BagAdapter(mutableListOf(), object : BagAdapter.OnQuantityChangeListener {
                override fun onQuantityChanged(
                    productId: String,
                    newQuantity: Int,
                    isPlus: Boolean,
                ) {
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
            when (paymentMethod) {
                "Cash on Delivery" -> {
                    val request = CreaterOrderRequest()
                    request.addressId = addressId
                    request.cartId = carId
                    request.accessToken = token
                    paymentViewModel.hipAPICreateCODOrder(request)

                }

                "Credit or Debit Card" -> {
                    val bundle = Bundle()
                    bundle.putString("addressId", addressId)
                    bundle.putString("cartId", carId)
                    bundle.putString("add", UserPreference.savedAddress)
                    bundle.putString("amount", amounts)
                    bundle.putString("currency", currency)

                    val request = PaymentIniRequest()
                    request.accessToken = token
                    request.addressId = addressId
                    request.cartId = carId
                    request.currency = currency
                    request.email = emailId
                    paymentViewModel.createPayStackOrder(request)
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.selected_method_is_not_cash_on_delivery),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setObserver() {
        bagViewModel.getCartListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.cart

                            if (data != null) {
                                val cost = data.cost
                                val edges = data.lines?.edges
                                CART_COUNT = edges?.size ?: 0
                                (requireActivity() as MainActivity).updateBagBadge(CART_COUNT)
                                amounts = cost?.totalAmount?.amount ?: ""
                                currency = cost?.totalAmount?.currencyCode ?: ""

                                binding.userAddressTv.text = UserPreference.savedAddress
                                binding.gpayTv.text = paymentMethod
                                val currentDateTime = SimpleDateFormat(
                                    "dd MMM yyyy, hh:mm a",
                                    Locale.getDefault()
                                ).format(Date())
                                binding.dateTv.text = currentDateTime

                                if (edges.isNullOrEmpty()) {
                                    binding.bagLayout.visibility = View.GONE
                                } else {
                                    binding.bagLayout.visibility = View.VISIBLE
                                    val totalAmount =
                                        "${cost?.totalAmount?.currencyCode} ${cost?.totalAmount?.amount}"
                                    binding.rsTv.text = totalAmount
                                    confirmOrderAdapter.updateBagItems(edges)
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

                    val statusCode = it.data?.code
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

        paymentViewModel.getCOD().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                            val orderId = it.data.payload.order.id.toString()
                            val bundle = Bundle()
                            bundle.putString("order_id", orderId)
                            findNavController().navigate(
                                R.id.action_confirmOrderFragment_to_congratulationFragment,
                                bundle
                            )
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

                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            val razor = it.data.payload.razorpay
                            val bundle = Bundle().apply {
                                putString("paymentUrl", razor.payment_link.short_url)
                                putString("successUrl", data.success_url_app)
                                putString("order_id", razor.order_id)
                            }

                            findNavController().navigate(
                                R.id.action_confirmOrderFragment_to_paymentGatewayFragment,
                                bundle
                            )
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
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
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}