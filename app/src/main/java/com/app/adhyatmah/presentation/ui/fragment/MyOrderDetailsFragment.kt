package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentMyOrderDetailsBinding
import com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_request.CancelOrderRequest
import com.app.adhyatmah.presentation.ui.adapter.MyOrderDetailsAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.ProfileViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.getValue

class MyOrderDetailsFragment : BaseFragment<FragmentMyOrderDetailsBinding>() {

    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private var myOrdersDetailsAdapter: MyOrderDetailsAdapter? = null
    val orderList =
        mutableListOf<com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details.Item>()
    var byOderId = ""
    var name = ""

    override fun setLayout(): Int {
        return R.layout.fragment_my_order_details
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView(savedInstanceState: Bundle?) {
        byOderId = arguments?.getString("orderId").toString()
        name = arguments?.getString("name").toString()
        binding.backImg.setOnClickListener {
            findNavController().navigateUp()
        }

        setObserver()
        profileViewModel.getOrderDetailsData(byOderId)

        binding.cancelBtn.setOnClickListener {
            val request = CancelOrderRequest(byOderId)
            profileViewModel.cancelOrderData(request)

        }

        myOrdersDetailsAdapter = MyOrderDetailsAdapter(orderList)
        binding.recyMyOrderDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.recyMyOrderDetails.adapter = myOrdersDetailsAdapter
        binding.copy.setOnClickListener {
            copyToClipboard(byOderId)
            binding.copy.text = getString(R.string.copied)
        }

    }

    private fun copyToClipboard(text: String) {
        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Order ID", text)
        clipboard.setPrimaryClip(clip)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        profileViewModel.getOrdersDetailsRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.order

                            if (data.status == "canceled") {
                                binding.cancled.visibility = View.VISIBLE
                                binding.cancelBtn.visibility = View.GONE
                            } else {
                                binding.cancled.visibility = View.GONE
                                binding.cancelBtn.visibility = View.VISIBLE

                            }
                            binding.orderId.text = data.orderNo
                            binding.priceTotal.text = data.currency + " " + data.total
                            binding.priceTv.text = data.currency + " " + data.subTotal
                            binding.taxAmount.text = data.currency + " " + data.tax
                            binding.orderTime.text = getTimeAgo(data.createdAt.toString())

                            binding.priceCoupons.text =
                                data.currency + " " + data.discount.toString()
                            binding.platformFees.text = data.currency + " " + data.platform_fee
                            binding.shippingCharges.text = data.currency + " " + data.shipping_fee
                            binding.cash.text = data.items?.getOrNull(0)?.deliveryType
                            binding.status.text = data.status
                            binding.address.text =
                                data.user?.firstName + " " + data.user?.lastName + "\n" + data.user?.address + "\n" + data.user?.city + "\n" + data.user?.state + "\n" + data.user?.country

                            val cateSetContainer = it.data.payload.order.items
                            myOrdersDetailsAdapter?.updateOrder(cateSetContainer ?: emptyList())
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

        profileViewModel.cancelOrderRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            val bundle = Bundle()
                            bundle.putString("type", "1")
                            findNavController().navigate(
                                R.id.action_myOrderDetailsFragment_to_myOrderFragment,
                                bundle
                            )
                            Log.d("tt", "sdsfdsfdsss, $data")
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }

                        422 -> {
                            Toast.makeText(
                                requireContext(),
                                it.data.message,
                                Toast.LENGTH_SHORT
                            ).show()
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
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeAgo(createdAt: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val time = LocalDateTime.parse(createdAt, formatter)
        val now = LocalDateTime.now(ZoneId.of("UTC"))

        val duration = Duration.between(time, now)

        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            duration.toDays() < 7 -> "${duration.toDays()} days ago"
            else -> time.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        }
    }
}