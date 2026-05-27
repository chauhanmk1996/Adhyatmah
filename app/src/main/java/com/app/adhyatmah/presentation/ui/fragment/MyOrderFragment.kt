package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.databinding.FragmentMyOrderBinding
import com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders.Order
import com.app.adhyatmah.domain.model.profile.manage_address.ManageAddressRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.adapter.MyOrdersAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.ProfileViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class MyOrderFragment : BaseFragment<FragmentMyOrderBinding>(){

    private val profileViewModel by activityViewModels<ProfileViewModel>()

    private lateinit var myOrdersAdapter: MyOrdersAdapter

    var token =""
    var type =""
    override fun setLayout(): Int {
        return R.layout.fragment_my_order
    }

    override fun initView(savedInstanceState: Bundle?) {

        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        type = arguments?.getString("type").toString()
       /* myOrdersAdapter = MyOrdersAdapter(mutableListOf())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myOrdersAdapter
        }
*/
        // Handle back button click
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.hereTv.setOnClickListener {
//            findNavController().navigate(R.id.action_myOrdersFragment_to_trackOrderFragment)
//            findNavController().navigate(R.id.action_myOrderFragment_to_myOrderDetailsFragment)

        }
        setupRecyclerView()
        setObserver()

       // profileViewModel.shippingUrlData()

        try {
            if (token.isEmpty()) {
                showLoginPrompt()
              //  Toast.makeText(requireContext(), "Data Not Found", Toast.LENGTH_SHORT).show()

            } else {
                var request = ManageAddressRequest(token)
                profileViewModel.getCustomerOrdersData(request)

            }
        }
        catch (e: Exception){
            Log.d("TAG","InitView: ${e.message}")
        }

        if (type == "1"){
            var request = ManageAddressRequest(token)
            profileViewModel.getCustomerOrdersData(request)

        }
    }
    private fun showLoginPrompt() {
        var dialog: AlertDialog? = null
        dialog=  CommonUtils.showCustomAlertDialog(
            requireActivity() ,
            "Sign Up Required",
            "Please sign up required to see my order.",
            positiveButtonText = "Sign up",
            negativeButtonText = "Cancel",
            positiveButtonAction = {
                dialog?.dismiss()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                val bundle = Bundle()
                requireActivity().startActivity(intent)            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }

    private fun setupRecyclerView() {
        myOrdersAdapter = MyOrdersAdapter(mutableListOf(), object : MyOrdersAdapter.OnMyOrderDetailsChangeListener {

            override fun onMyOrderDeChanged(order: Order) {
               // Toast.makeText(requireContext(), "Under Development",Toast.LENGTH_SHORT).show()
                  val bundle = Bundle()
//                  bundle.putString("orderId",order.id.toString())
//                  bundle.putString("name", order.shipping_address?.customer_name)
                bundle.putString("orderId",order.orderId)
                bundle.putString("name", order.title)
                  findNavController().navigate(R.id.action_myOrderFragment_to_myOrderDetailsFragment,bundle)

            }

            override fun onTrackClicked(orderId: String) {
                val bundle = Bundle().apply {
                    putString("orderId", orderId)
                }
                findNavController().navigate(R.id.action_myOrderFragment_to_trackOrderFragment, bundle)
            }

          /*  override fun onBagEmpty() {
                binding.itemsBagRecycler.visibility = View.INVISIBLE
                binding.emptyTextView.visibility = View.VISIBLE // Add this TextView in XML
            }
*/
        })


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myOrdersAdapter

        }
    }


    private fun setObserver(){

        profileViewModel.getCustomerOrdersRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val orders = it.data?.payload?.orders
                            if (orders != null && orders.isNotEmpty()) {
                                myOrdersAdapter.updateOrders(orders)
                                binding.textNoOrder.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                            } else {
                               // myOrdersAdapter.updateOrders(emptyList())
                                binding.textNoOrder.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                                // Handle empty list or null response gracefully
                                Log.w("MyOrderFragment", "Orders list is null. Showing empty state.")
                            }
                           /* var data = it.data.payload.orders
                            val cateSetContainer = it.data.payload.orders
                            myOrdersAdapter.updateOrders(cateSetContainer)
*/

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