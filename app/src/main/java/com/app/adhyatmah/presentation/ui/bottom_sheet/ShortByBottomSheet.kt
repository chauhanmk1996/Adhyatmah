package com.app.adhyatmah.presentation.ui.bottom_sheet

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.DataString
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.FragmentShortByBottomSheetBinding
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.domain.model.view_all_product.response.Product
import com.app.adhyatmah.presentation.ui.adapter.AdapterBottomSheetShortBy
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseBottomSheetFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class ShortByBottomSheet() :BaseBottomSheetFragment<FragmentShortByBottomSheetBinding>() {

    var productId = ""
    private var list: MutableList<DataString> = mutableListOf()
    private val viewModel by activityViewModels<FilterViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()

    var token = ""

    private lateinit var adapter: AdapterBottomSheetShortBy
    interface OnSortItemSelectedListener {
        fun onSortItemSelected(item: List<Product>)
    }

    private var listener: OnSortItemSelectedListener? = null
    companion object {
        fun newInstance(productId: String): ShortByBottomSheet {
            return ShortByBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(PRODUCT_ID, productId)
                }
            }
        }
    }

    var onSortSelected: ((List<Product>) -> Unit)? = null
    var onSortRequestSent: ((ViewAllProductRequest) -> Unit)? = null
    fun setOnSortItemSelectedListener(listener: OnSortItemSelectedListener) {
        this.listener = listener
    }
    private fun setList() {

        list.clear()
        list.addAll(
            mutableListOf(
                DataString("Popular",false),
                DataString("Newest",false),
                DataString("Customer review",false),
                DataString("Price: lowest to high",false),
                DataString("Price: highest to low",false)
            )
        )


        adapter = AdapterBottomSheetShortBy(list) {
            selectedItem->
            //dismiss()
            val result = Bundle().apply {
                val sortKey = when (selectedItem.title) {
                    "Popular" -> "popular"
                    "Newest" -> "newest"
                    "Customer review" -> "customer_review"
                    "Price: lowest to high" -> "price_low_high"
                    "Price: highest to low" -> "price_high_low"
                    else -> ""
                }
                putString("selected_sort_title", sortKey)

                var request = ViewAllProductRequest(
                    productId,
                    sortKey
                )
                onSortRequestSent?.invoke(request)  // ✅ Call higher-order function
                dismiss()
//                homeViewModel.getViewAllData(token,request)
                /*var request = GetSortedCollectionRequest()
                request.handle= productId
                request.sortBy=   sortKey
                viewModel.homeCollectionApi(request)
         */   }
            parentFragmentManager.setFragmentResult("sort_result_key", result)
           // dismiss()

           // listener?.onSortItemSelected(selectedItem)

        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = adapter
    }

    override fun setLayout(): Int {
        return R.layout.fragment_short_by_bottom_sheet
    }

    override fun initView(savedInstanceState: Bundle?) {

        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        arguments?.getString(PRODUCT_ID)?.let {
            productId = it
            Log.d("ShortByBottomSheet", "Received productId: $productId")
        } ?: run {
            Log.e("ShortByBottomSheet", "No productId received")
        }

        setList()
        setObserver()
    }

    override fun getTheme(): Int = R.style.TransparentBottomSheetDialog

    private fun setObserver(){

        homeViewModel.getViewAllLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.status // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            Log.i("TAG", "setObserver: getViewAllLiveData")
                            val data = it.data.payload.collection.products
                            onSortSelected?.invoke(data)  // ✅ Call higher-order function
                            dismiss()
//                            setAdapter(data)
//                            adapter.updateData(data)
                            // Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()

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


        viewModel.getShortCollectionList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.collection.products

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
