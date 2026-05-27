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
import androidx.recyclerview.widget.GridLayoutManager
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.databinding.FragmentWishListBinding
import com.app.adhyatmah.domain.model.fetch_wish_data.Wishlist
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.adapter.WishListAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue


class WishListFragment : BaseFragment<FragmentWishListBinding>() {

    private val filterViewModel by activityViewModels<FilterViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()

    lateinit var wishListAdapter: WishListAdapter
    val productList = mutableListOf<Wishlist>()

//    var token = "b3cf17424bb2a94016acd98fc22e6f1d"
    var token = ""


    override fun setLayout(): Int {
        return R.layout.fragment_wish_list
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initView(savedInstanceState: Bundle?) {

      token = Preferences.getStringPreference(requireActivity(), ACCESS_TOKEN).toString()
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }


        try {
            if (token.isNullOrEmpty()){
                showLoginPrompt()
              //  Toast.makeText(requireActivity(), "Data Not Found", Toast.LENGTH_SHORT).show()
            }
            else{
                setObserve()
                filterViewModel.getWishListsData(token)
            }

        }
        catch (e: Exception){
            Log.d("TAG","initView: ${e.message}")
        }

        setAdapter()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun showLoginPrompt() {
        var dialog: AlertDialog? = null
        dialog=  CommonUtils.showCustomAlertDialog(
            requireActivity() ,
            "Sign Up Required",
            "Please sign up required to see  wishlist.",
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

    private fun setAdapter() {

        wishListAdapter = WishListAdapter(productList.toMutableList()) { item, position ->
            val request = AddWishListRequest(token, item.id) // Or your removal request
            homeViewModel.removeWishLisData(request)
            wishListAdapter.removeItem(position)

        }
        binding.reyWishList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.reyWishList.adapter = wishListAdapter

    }


    private fun setObserve(){
        
        filterViewModel.getWishListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data

                            val cateSetContainer = it.data.payload.wishlist
                            if (cateSetContainer.isNullOrEmpty()) {
                                binding.reyWishList.visibility = View.GONE
                                binding.text.visibility = View.VISIBLE
                            } else {
                                binding.reyWishList.visibility = View.VISIBLE
                                binding.text.visibility = View.GONE
                                wishListAdapter.updateItems(cateSetContainer)
                            }
                            /*if(cateSetContainer.isNullOrEmpty() && cateSetContainer==null){
                                binding.reyWishList.visibility = View.GONE
                                binding.text.visibility = View.VISIBLE
                            }else{
                                binding.reyWishList.visibility = View.VISIBLE
                                binding.text.visibility = View.GONE

                                wishListAdapter.updateItems(cateSetContainer)
                            }*/

                            Log.d("tt","sdsfdsf, $data")
                            Log.d("Tdkjd",data.toString())
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

        homeViewModel.removeWishList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val data = it.data?.payload

//                    val message = it.message ?: "Something went wrong"
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    filterViewModel.getWishListsData(token)

                    Toast.makeText(requireActivity(), "Remove from wishlist", Toast.LENGTH_SHORT).show()

                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), it.message ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    }

}