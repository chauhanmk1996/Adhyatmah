package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.databinding.FragmentCategoryProductBinding
import com.app.adhyatmah.domain.model.AllCategoryListResponse
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.domain.model.view_all_product.response.Product
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.presentation.ui.adapter.AdapterViewAllProduct
import com.app.adhyatmah.presentation.ui.adapter.AllCategoryListAdapter
import com.app.adhyatmah.presentation.ui.bottom_sheet.ShortByBottomSheet
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.Status

class CategoryProductFragment : BaseFragment<FragmentCategoryProductBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private var passedCategoryHandle: String = ""
    private var selectedCategoryHandle: String = ""
    private lateinit var categoryAdapter: AllCategoryListAdapter
    private lateinit var productAdapter: AdapterViewAllProduct
    private val categories = mutableListOf<AllCategoryListResponse.Collection>()
    private val products = mutableListOf<Product>()

    override fun setLayout(): Int = R.layout.fragment_category_product

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.getString("category_handle")?.let {
            passedCategoryHandle = it
        }
        setupCategoryList()
        setupProductList()
        clicks()
        homeViewModel.getAllCtData(page = 1, limit = 100)  // load all categories first
    }

    private val filterViewModel by activityViewModels<FilterViewModel>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCategoryData()
        observeProductData()
        observeSortingData()
    }

    private fun setupCategoryList() {
        categoryAdapter = AllCategoryListAdapter(categories) { selectedCategory ->
            selectedCategoryHandle = selectedCategory.handle
            loadProducts(selectedCategory.handle)
        }
        binding.recyCategoryLeft.adapter = categoryAdapter
    }

    private fun setupProductList() {
        productAdapter = AdapterViewAllProduct(
            subList = products,
            showImage = true,
            onWishlistClick = { pos, like -> onLikePressed(pos, like) },
            onSubAdapterClick = { pos, _, data -> openProductDetail(data) }
        )

        binding.recyProductsRight.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun loadProducts(categoryHandle: String) {
        startShimmer()
        homeViewModel.getViewAllData(
            Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString(),
            ViewAllProductRequest(categoryHandle)
        )
    }

    private fun observeCategoryData() {
        homeViewModel.getAllCatLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    stopShimmer()
                    val list = it.data?.payload?.collections ?: emptyList()
                    categories.clear()
                    categories.addAll(list)
                    categoryAdapter.updateItems(list)
                    Log.i("TAG", "observeCategoryData: " + list)

                    if (list.isNotEmpty()) {
                        if (passedCategoryHandle.isNotEmpty()) {
                            val selectedIndex =
                                list.indexOfFirst { it.handle == passedCategoryHandle }

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
                    }
                }

                Status.LOADING -> startShimmer()
                Status.ERROR -> stopShimmer()
            }
        }
    }

    private fun observeProductData() {
        homeViewModel.getViewAllLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    stopShimmer()
                    val list = it.data?.payload?.collection?.products ?: emptyList()
                    products.clear()
                    products.addAll(list)
                    productAdapter.updateData(products)
                }

                Status.LOADING -> startShimmer()
                Status.ERROR -> stopShimmer()
            }
        }
    }

    private fun observeSortingData() {
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