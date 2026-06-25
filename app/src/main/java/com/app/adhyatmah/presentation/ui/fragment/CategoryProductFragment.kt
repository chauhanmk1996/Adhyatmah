package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.CURRENT_PINCODE
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentCategoryProductBinding
import com.app.adhyatmah.domain.model.AllCategoryListResponse
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_request.AddToBagRequest
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.domain.model.view_all_product.response.Product
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.AdapterViewAllProduct
import com.app.adhyatmah.presentation.ui.adapter.AllCategoryListAdapter
import com.app.adhyatmah.presentation.ui.bottom_sheet.ShortByBottomSheet
import com.app.adhyatmah.presentation.ui.bottom_sheet.SignUpRequiredBottomSheetFragment
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class CategoryProductFragment : BaseFragment<FragmentCategoryProductBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val filterViewModel by activityViewModels<FilterViewModel>()
    var token = ""
    private var passedCategoryHandle: String = ""
    private var selectedCategoryHandle: String = ""
    private lateinit var categoryAdapter: AllCategoryListAdapter
    private lateinit var productAdapter: AdapterViewAllProduct
    private val categories = mutableListOf<AllCategoryListResponse.Collection>()
    private val products = mutableListOf<Product>()

    override fun setLayout(): Int = R.layout.fragment_category_product

    override fun initView(savedInstanceState: Bundle?) {
        observeData()
        arguments?.getString("category_handle")?.let {
            passedCategoryHandle = it
        }
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        setupCategoryList()
        setupProductList()
        clicks()
        startShimmer()
        homeViewModel.getAllCtData(page = 1, limit = 100)
    }

    private fun observeData() {
        homeViewModel.getAllCatLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val list = it.data?.payload?.collections ?: emptyList()
                    categories.clear()
                    categories.addAll(list)
                    categoryAdapter.updateItems(list)

                    if (list.isNotEmpty()) {
                        if (passedCategoryHandle.isNotEmpty()) {
                            val selectedIndex =
                                list.indexOfFirst { passed -> passed.handle == passedCategoryHandle }

                            if (selectedIndex != -1) {
                                selectedCategoryHandle = passedCategoryHandle
                                categoryAdapter.setSelectedCategory(selectedIndex)
                                loadProducts(selectedCategoryHandle)
                            } else {
                                selectedCategoryHandle = list[0].handle
                                categoryAdapter.setSelectedCategory(0)
                                loadProducts(selectedCategoryHandle)
                            }
                        } else {
                            selectedCategoryHandle = list[0].handle
                            categoryAdapter.setSelectedCategory(0)
                            loadProducts(list[0].handle)
                        }
                    } else {
                        stopShimmer()
                    }
                }

                Status.LOADING -> {}
                Status.ERROR -> stopShimmer()
            }
        }

        homeViewModel.getViewAllLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val list = it.data?.payload?.collection?.products ?: emptyList()
                    products.clear()
                    products.addAll(list)
                    productAdapter.updateData(products)
                    stopShimmer()
                }

                Status.LOADING -> {}
                Status.ERROR -> stopShimmer()
            }
        }

        filterViewModel.getShortCollectionList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val code = it.data?.code
                    if (code == 200) {
                        val sorted = it.data.payload.collection.products
                        products.clear()
                        products.addAll(sorted)
                        productAdapter.updateData(products)
                    }
                }

                Status.LOADING -> {}
                Status.ERROR -> {}
            }
        }

        homeViewModel.getAddToBag().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val message = it.data.message
                            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                            Preferences.setStringPreference(
                                requireContext(),
                                CART_ID,
                                it.data.payload.cart.id
                            )
                            CART_COUNT = it.data.payload.cart.lines.edges.size
                            (requireActivity() as MainActivity).updateBagBadge(CART_COUNT)
                            (requireActivity() as MainActivity).switchToCartTab()
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

    private fun setupCategoryList() {
        categoryAdapter = AllCategoryListAdapter(categories) { selectedCategory ->
            products.clear()
            productAdapter.clearList()
            selectedCategoryHandle = selectedCategory.handle
            loadProducts(selectedCategory.handle)
        }
        binding.rvCategory.adapter = categoryAdapter
    }

    private fun setupProductList() {
        productAdapter = AdapterViewAllProduct(
            subList = products,
            showImage = true,
            onWishlistClick = { pos, like -> onLikePressed(pos, like) },
            onSubAdapterClick = { _, _, data -> openProductDetail(data) },
            onAddToCartClick = ::onAddToCartClick
        )

        binding.rvProduct.adapter = productAdapter
    }

    private fun onAddToCartClick(product: Product) {
        var availablePinCode: List<String> = listOf()
        if (product.pincode?.isNotEmpty() == true) availablePinCode = product.pincode
        if (token.isEmpty()) {
            signupRequired(getString(R.string.please_sign_up_to_add_items_to_your_bag))
        } else if (product.stockQuantity == 0) {
            Toast.makeText(
                requireContext(),
                getString(R.string.product_not_available), Toast.LENGTH_SHORT
            )
                .show()
        } else if ((availablePinCode.isEmpty()) || (availablePinCode.any {
                it.equals(
                    Preferences.getStringPreference(requireContext(), CURRENT_PINCODE),
                    ignoreCase = true
                )
            })) {
            val request = AddToBagRequest()
            request.accessToken = token
            request.quantity = 1
            request.variantId = product.id
            homeViewModel.addToBagApi(request)
        } else {
            showPinCodePrompt()
        }
    }

    private fun showPinCodePrompt() {
        var dialog: AlertDialog? = null
        dialog = CommonUtils.showPinCodeDialog(
            requireActivity(),
            getString(R.string.alert),
            getString(R.string.product_is_not_available_for_your_current_location),
            positiveButtonText = getString(R.string.ok),
            negativeButtonText = "",
            positiveButtonAction = {
                dialog?.dismiss()
            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
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

    private fun clicks() {
        binding.shortLayout.setOnClickListener {
            if (selectedCategoryHandle.isNotEmpty()) {
                showSortBottomSheet(selectedCategoryHandle)
            }
        }
    }

    private fun showSortBottomSheet(categoryHandle: String) {
        val sheet = ShortByBottomSheet.newInstance(categoryHandle)
        sheet.onSortRequestSent = { request ->
            homeViewModel.getViewAllData(
                Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString(),
                request
            )
        }

        sheet.onSortSelected = { sortedList ->
            products.clear()
            products.addAll(sortedList)
            productAdapter.updateData(products)
        }
        sheet.show(parentFragmentManager, "ShortByBottomSheet")
    }

    private fun loadProducts(categoryHandle: String) {

        homeViewModel.getViewAllData(
            Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString(),
            ViewAllProductRequest(categoryHandle)
        )
    }

    private fun startShimmer() {
        binding.myPropertyShimmer.myPropertyMainShimmer.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        binding.myPropertyShimmer.myPropertyMainShimmer.visibility = View.GONE
    }

    private fun onLikePressed(position: Int, isLike: Boolean) {
        val product = productAdapter.getItemAt(position)
        val req = AddWishListRequest(
            Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString(),
            product.id
        )
        if (isLike) homeViewModel.addWishLisData(req)
        else homeViewModel.removeWishLisData(req)
    }

    private fun openProductDetail(data: Product) {
        val bundle = Bundle()
        bundle.putString(TYPE, "3")
        bundle.putString(PRODUCT_ID, data.id)
        findNavController().navigate(
            R.id.productDetailsFragment,
            bundle
        )
    }
}