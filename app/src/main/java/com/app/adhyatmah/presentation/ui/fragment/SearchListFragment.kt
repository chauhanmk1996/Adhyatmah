package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.SEARCH_PRODUCT_ID
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.databinding.FragmentSearchListBinding
import com.app.adhyatmah.domain.model.search_list_response.search_list_api_response.Result
import com.app.adhyatmah.domain.model.search_list_response.search_list_request.SearchListRequest
import com.app.adhyatmah.presentation.ui.adapter.SearchListAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue


class SearchListFragment : BaseFragment<FragmentSearchListBinding>() {
    private val homeViewModel by activityViewModels<HomeViewModel>()

    private lateinit var searchListAdapter: SearchListAdapter
    val searchList = mutableListOf<Result>()

    var productId = ""
    private var selectedType: String = "PRODUCT" // Default


    override fun setLayout(): Int {
        return R.layout.fragment_search_list
    }
    private fun showLoading() {
        binding.loadingLayout.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingLayout.visibility = View.GONE
    }

    override fun initView(savedInstanceState: Bundle?) {

        /*val items = listOf("Product","Brand", "Category")
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.mySpinner.adapter = adapter
        */

        setObserver()
        setAdapter()
        homeViewModel.getSearchTypeListData()
//      searchProducts("dress")

        binding.backImg.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchView.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                if(s.isNullOrEmpty()){
                    binding.noResult.visibility = View.VISIBLE
                    binding.recSearch.visibility = View.GONE
                }else{
                    if(query.length >= 2)
                    {
                        searchProducts(query)
                    }
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

}

    private fun searchProducts(query: String) {
        binding.noResult.visibility = View.GONE
        val request = SearchListRequest(
            first = 200,
            query = query,
            sortKey = "RELEVANCE",
            types = listOf(selectedType)
        )
        homeViewModel.getSearchListData(request)
    }

    private fun setSpinnerOptions(options: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            options
        )
        binding.mySpinner.adapter = adapter

        binding.mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedType = options[position] // Save selected type

                Log.d("TAG","SpinnerItem: $selectedType")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional
            }
        }
    }
    private fun setAdapter() {

        searchListAdapter = SearchListAdapter(searchList, object : SearchListAdapter.OnItemClickListener{
            override fun onItemClick(productId:Result) {
                val bundle = Bundle()
                bundle.putString(TYPE,"4")
                bundle.putString(SEARCH_PRODUCT_ID,productId.id)
                findNavController().navigate(R.id.action_searchListFragment_to_productDetailsFragment,bundle)
            }

        })
        binding.recSearch.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recSearch.adapter = searchListAdapter

    }
    private fun setObserver() {

        homeViewModel.getSearchListRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    hideLoading()
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.results
                            Log.d("TAG", "setObsdderver:$data ")
                            val searchContainer = it.data.payload.results
                            if(searchContainer.isNullOrEmpty()){
                                binding.noResult.visibility = View.VISIBLE
                                binding.recSearch.visibility = View.GONE
                            }else{
                                binding.noResult.visibility = View.GONE
                                binding.recSearch.visibility = View.VISIBLE
                                searchListAdapter.updateItems(searchContainer)

                            }

                            Log.d("Tag","SearchInitView : $data")
                        }
                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    showLoading()
//                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

        homeViewModel.getSearchTypeListResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {

                            val typesList = it.data.payload.types
                            Log.d("Tag", "SearchTypeInitView : $typesList")
                            setSpinnerOptions(typesList)
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