package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.CATEGORY_TITLE
import com.app.adhyatmah.data.preferences.PRODUCT_TITLE
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.databinding.FragmentCategoryBinding
import com.app.adhyatmah.domain.model.AllCategoryListResponse
import com.app.adhyatmah.presentation.ui.adapter.AllCategoryListAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var categoryListAdapter: AllCategoryListAdapter
    private val productList = mutableListOf<AllCategoryListResponse.Collection>()
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private val limitPerPage = 10

    override fun setLayout(): Int = R.layout.fragment_category

    override fun initView(savedInstanceState: Bundle?) {
        setupAdapter()
        observeData()
        fetchCategories()

        binding.recyCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && !isLastPage && lastVisibleItemPosition >= totalItemCount - 1) {
                    loadNextPage()
                }
            }
        })

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                categoryListAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun setupAdapter() {
        categoryListAdapter = AllCategoryListAdapter(productList) { item ->
            val handle = item.handle
            val title = item.title
            val bundle = Bundle().apply {
                putString(TYPE, "1")
                putString(CATEGORY_TITLE, handle)
                putString(PRODUCT_TITLE, title)
            }
            Log.d("CategoryFragment", "Navigating with handle: $handle")
            findNavController().navigate(
                R.id.action_categoryFragment_to_productListFragment,
                bundle
            )
        }

        binding.recyCategory.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyCategory.adapter = categoryListAdapter
        categoryListAdapter.updateItems(productList)

    }

    private fun fetchCategories() {
        homeViewModel.getAllCtData(currentPage, limitPerPage)
    }

    private fun loadNextPage() {
        if (isLoading || isLastPage) return
        isLoading = true
        currentPage++
        fetchCategories()
    }

    private fun startShimmerLayout() {
        binding.myPropertyShimmer.myPropertyMainShimmer.startShimmer()
        binding.myPropertyShimmer.myPropertyMainShimmer.visibility = View.VISIBLE
        binding.scrollId.visibility = View.GONE
    }

    private fun stopShimmer() {
        binding.myPropertyShimmer.myPropertyMainShimmer.stopShimmer()
        binding.myPropertyShimmer.myPropertyMainShimmer.visibility = View.GONE
        binding.scrollId.visibility = View.VISIBLE
    }

    private fun observeData() {
        homeViewModel.getAllCatLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (currentPage == 1) startShimmerLayout()
                }

                Status.SUCCESS -> {
                    stopShimmer()
                    isLoading = false

                    val statusCode = it.data?.code
                    if (statusCode == 200) {
                        val collections = it.data.payload.collections
                        if (collections.size < limitPerPage) {
                            isLastPage = true
                        }

                        categoryListAdapter.updateItems(collections)
                    } else if (statusCode == 401) {
                        Log.e("CategoryFragment", "Unauthorized access")
                    }
                }

                Status.ERROR -> {
                    stopShimmer()
                    isLoading = false
                    Snackbar.make(
                        requireView(),
                        it.message ?: "Unknown error",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}