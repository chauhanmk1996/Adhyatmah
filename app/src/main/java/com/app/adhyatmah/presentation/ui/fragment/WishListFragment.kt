package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.app.adhyatmah.presentation.ui.bottom_sheet.SignUpRequiredBottomSheetFragment
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class WishListFragment : BaseFragment<FragmentWishListBinding>() {

    private val filterViewModel by activityViewModels<FilterViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    lateinit var wishListAdapter: WishListAdapter
    val productList = mutableListOf<Wishlist>()
    var token = ""

    override fun setLayout(): Int {
        return R.layout.fragment_wish_list
    }

    override fun initView(savedInstanceState: Bundle?) {
        token = Preferences.getStringPreference(requireActivity(), ACCESS_TOKEN).toString()

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        try {
            if (token.isEmpty()) {
                signupRequired(getString(R.string.please_sign_up_required_to_see_wishlist))
            } else {
                setObserve()
                filterViewModel.getWishListsData(token)
            }
        } catch (e: Exception) {
            Log.d("TAG", "initView: ${e.message}")
        }
        setAdapter()
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

    private fun setAdapter() {
        wishListAdapter = WishListAdapter(productList.toMutableList()) { item, position ->
            val request = AddWishListRequest(token, item.id) // Or your removal request
            homeViewModel.removeWishLisData(request)
            wishListAdapter.removeItem(position)

        }
        binding.reyWishList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.reyWishList.adapter = wishListAdapter
    }


    private fun setObserve() {
        filterViewModel.getWishListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val cateSetContainer = it.data.payload.wishlist
                            if (cateSetContainer.isEmpty()) {
                                binding.reyWishList.visibility = View.GONE
                                binding.text.visibility = View.VISIBLE
                            } else {
                                binding.reyWishList.visibility = View.VISIBLE
                                binding.text.visibility = View.GONE
                                wishListAdapter.updateItems(cateSetContainer)
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

        homeViewModel.removeWishList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    filterViewModel.getWishListsData(token)
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.remove_from_wishlist), Toast.LENGTH_SHORT
                    ).show()
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), it.message ?: "", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}