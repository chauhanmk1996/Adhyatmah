package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.data.preferences.PRODUCT_TITLE
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.CATEGORY_TITLE
import com.app.adhyatmah.data.preferences.CURRENT_PINCODE
import com.app.adhyatmah.data.preferences.HANDLER
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.MENU_TITLE
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentProductListBinding
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_request.AddToBagRequest
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.domain.model.view_all_product.response.Product
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.AdapterViewAllProduct
import com.app.adhyatmah.presentation.ui.bottom_sheet.ShortByBottomSheet
import com.app.adhyatmah.presentation.ui.bottom_sheet.SignUpRequiredBottomSheetFragment
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.capitalizeFirst
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class ProductListFragment : BaseFragment<FragmentProductListBinding>() {

    lateinit var adapter: AdapterViewAllProduct
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val filterViewModel by activityViewModels<FilterViewModel>()
    var categoryHandle = ""
    var collectionHandle = ""
    var title = ""
    var manuTitle = ""
    var productId = ""
    var token = ""
    var discardType = ""
    var fromFilter = ""
    var type = ""

    override fun setLayout(): Int {
        return R.layout.fragment_product_list
    }

    override fun initView(savedInstanceState: Bundle?) {
        type = arguments?.getString(TYPE).toString()
        discardType = arguments?.getString("discardType").toString()
        categoryHandle = arguments?.getString(CATEGORY_TITLE).toString()
        collectionHandle = arguments?.getString(MENU_TITLE).toString()
        manuTitle = arguments?.getString("TITLE").toString()
        title = arguments?.getString(PRODUCT_TITLE).toString()

        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        setObserver()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        filterAction()
        fromWhere(type, fromFilter)

        try {
            if (type == "1") {
                binding.shortLayout.setOnClickListener {
                    Log.d("TAG", "filterAction: $productId $categoryHandle")
                    showSortBottomSheet(categoryHandle)
                }
            } else if (type == "2") {
                binding.shortLayout.setOnClickListener {
                    Log.d("TAG", "filterAction: $productId $collectionHandle")
                    showSortBottomSheet(collectionHandle)
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "InitView: ${e.message}")
        }
    }

    fun fromWhere(type: String, fromFilter: String) {
        when (type) {
            "1" -> {
                binding.tvHeading.text = title
                productId = categoryHandle
                productId = categoryHandle
                homeViewModel.getViewAllData(token, ViewAllProductRequest(categoryHandle))

                when (fromFilter) {
                    "fromFilter" -> {
                        // Do nothing; filtered data already observed
                    }

                    "fromDisCart" -> {
                        homeViewModel.getViewAllData(token, ViewAllProductRequest(categoryHandle))
                    }

                    else -> {
                        homeViewModel.getViewAllData(token, ViewAllProductRequest(categoryHandle))
                    }
                }
            }

            "2" -> {
                binding.tvHeading.text = manuTitle
                homeViewModel.getViewAllData(token, ViewAllProductRequest(collectionHandle))
                productId = categoryHandle

                when (fromFilter) {
                    "fromFilter" -> {

                    }

                    "fromDisCart" -> {
                        homeViewModel.getViewAllData(token, ViewAllProductRequest(collectionHandle))
                    }

                    else -> {
                        homeViewModel.getViewAllData(token, ViewAllProductRequest(collectionHandle))
                    }
                }
                productId = collectionHandle

            }

            else -> {
                val title = arguments?.getString(PRODUCT_TITLE).toString()
                val productId = arguments?.getString(HANDLER).toString()
                this.productId = productId

                binding.tvHeading.text = title
                homeViewModel.getViewAllData(token, ViewAllProductRequest(productId))

                when (fromFilter) {
                    "fromFilter" -> {
                        homeViewModel.getViewAllData(token, ViewAllProductRequest(productId, ""))
                    }

                    "fromDisCart" -> {
                        homeViewModel.getViewAllData(token, ViewAllProductRequest(productId))
                    }

                    else -> {
                        homeViewModel.getViewAllData(token, ViewAllProductRequest(productId))
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        fromWhere(type, fromFilter)
        setObserver()
    }

    fun setAdapter(data: List<Product>) {
        adapter = AdapterViewAllProduct(
            data, true, ::isLikeClick,
            onSubAdapterClick = ::onSubAdapterImgClick, onAddToCartClick = ::onAddToCartClick
        )
        binding.productListRecyclerView.adapter = adapter
    }

    fun isLikeClick(position: Int, isLike: Boolean) {
        val product = adapter.getItemAt(position)
        val request = AddWishListRequest(token, product.id)
        val isLogin = Preferences.getStringPreference(requireContext(), IS_LOGIN)

        if (isLogin == "1") {
            if (isLike) {
                homeViewModel.addWishLisData(request)
            } else {
                homeViewModel.removeWishLisData(request)
            }

        } else {
            signupRequired(getString(R.string.please_sign_up_to_add_items_to_your_wishlist))
        }
    }

    fun onSubAdapterImgClick(position: Int, isTrue: Boolean, data: Product) {
        Log.d("TAG", "onSubAdapterImgClick: ${data.id}")
        val productId = data.id
        val bundle = Bundle()
        bundle.putString(TYPE, "3")
        bundle.putString(PRODUCT_ID, productId)
        findNavController().navigate(
            R.id.action_productListFragment_to_productDetailsFragment,
            bundle
        )
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

    private fun setObserver() {
        homeViewModel.getViewAllLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.status
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.collection.products
                            setAdapter(data)
                            adapter.updateData(data)
                        }

                        401 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        404 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
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

        filterViewModel.getShortCollectionList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    if (statusCode == 200) {
                        val sortedProducts = it.data.payload.collection.products
                        setAdapter(sortedProducts)
                    } else {
                        Log.e("TAG", "Sorting API returned non-200")
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

        filterViewModel.getApplyFilter().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    if (statusCode == 200) {
                        val sortedProducts = it.data.payload.collection.products
                        setAdapter(sortedProducts)
                    } else {
                        Log.e("TAG", "Sorting API returned non-200")
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
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(
                        requireView(),
                        it.message ?: "Unknown error",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
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

    fun filterAction() {
        binding.shortLayout.setOnClickListener {
            val handle = arguments?.getString(HANDLER).toString()
            val productId = arguments?.getString(PRODUCT_TITLE).toString()
            Log.d("TAG", "filterAction: $productId $handle")
            showSortBottomSheet(handle)
        }
    }

    private fun showSortBottomSheet(productId: String) {
        val bottomSheet = ShortByBottomSheet.newInstance(productId)
        bottomSheet.onSortSelected = { sortedList ->
            adapter.updateData(sortedList)
        }
        bottomSheet.onSortRequestSent = { request ->
            binding.shortText.text = request.sortBy?.capitalizeFirst() ?: ""
            homeViewModel.getViewAllData(token, request)
        }
        bottomSheet.show(parentFragmentManager, "ShortByBottomSheet")
    }
}