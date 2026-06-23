package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.DataString
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CATEGORY_HANDLE_FILTER
import com.app.adhyatmah.data.preferences.FROM_FILTER
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.FragmentFilterBinding
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.presentation.ui.adapter.CategoryListAdapter
import com.app.adhyatmah.presentation.ui.adapter.ColorsListAdapter
import com.app.adhyatmah.presentation.ui.adapter.SizesAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class FilterFragment : BaseFragment<FragmentFilterBinding>() {

    private lateinit var colorsAdapter: ColorsListAdapter
    private lateinit var sizesAdapter: SizesAdapter
    private lateinit var categoryListAdapter: CategoryListAdapter
    private val colorsList = mutableListOf<DataString>()
    private val sizesList = mutableListOf<DataString>()
    private val categoryList = mutableListOf<DataString>()
    private val viewModel by activityViewModels<FilterViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private var isFilterLoaded = false
    val selectedColors = mutableListOf<String>()
    val selectedSizes = mutableListOf<String>()
    val selectedCategories = mutableListOf<String>()
    var brandList = mutableListOf<String>()
    var productId = ""
    var categoryHandle = ""
    var collectionHandle = ""
    var title = ""
    var token = ""

    override fun setLayout(): Int {
        return R.layout.fragment_filter
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initView(savedInstanceState: Bundle?) {

        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()

        selectedColors.clear()
        selectedColors.addAll(viewModel.selectedColors)

        selectedSizes.clear()
        selectedSizes.addAll(viewModel.selectedSizes)

        selectedCategories.clear()
        selectedCategories.addAll(viewModel.selectedCategories)

        brandList.clear()
        brandList.addAll(viewModel.selectedBrands)

        setObserver()
        setupClickListeners()
        val selectedBrands = arguments?.getStringArray("SELECTED_BRANDS")?.toList() ?: emptyList()
        Log.d("FilterFragment", "Selected brands received: $selectedBrands")

        val type = arguments?.getString("TYPE").toString()
        productId = arguments?.getString(PRODUCT_ID).toString()
        categoryHandle = arguments?.getString(CATEGORY_HANDLE_FILTER).toString()
        collectionHandle = arguments?.getString("menu_handle").toString()

        if (!isFilterLoaded) {
            viewModel.hitGetFilterData()
            isFilterLoaded = true
        }

        hitApi(productId)
        setupClickListeners()
        Log.d("TAG", "initcdcView: $brandList")
        Log.d("tt", "iivdhdsvkjvx: $categoryHandle")

        try {
            if (type == "5") {
                hitApi(categoryHandle)
            }

            if (type == "6")
                hitApi(collectionHandle)
        } catch (e: Exception) {
            Log.d("TAG", "Unauth: ${e.message}")
        }

        binding.seekBarId.apply {
            valueFrom = 0f
            valueTo = 50000f
            stepSize = 10f

            val minPrice = viewModel.minPrice.takeIf { it > 0f } ?: 0f
            val maxPrice = viewModel.maxPrice.takeIf { it > 0f } ?: 50000f
            values = listOf(minPrice, maxPrice)

            binding.minText.text = "$${minPrice.toInt()}"
            binding.maxText.text = "$${maxPrice.toInt()}"

            setLabelFormatter { value ->
                "$${value.toInt()}"
            }

            addOnChangeListener { slider, _, _ ->
                val min = slider.values[0]
                val max = slider.values[1]
                binding.minText.text = "$${min.toInt()}"
                binding.maxText.text = "$${max.toInt()}"
            }
        }

        binding.discardTv.setOnClickListener {

            viewModel.selectedColors.clear()
            viewModel.selectedSizes.clear()
            viewModel.selectedCategories.clear()
            viewModel.selectedBrands.clear()
            viewModel.minPrice = 0f
            viewModel.maxPrice = 0f

            isFilterLoaded = false


            val handleToUse = when {
                productId.isNotBlank() && productId != "null" -> productId
                categoryHandle.isNotBlank() && categoryHandle != "null" -> categoryHandle
                else -> {
                    Snackbar.make(requireView(), getString(R.string.something_went_wrong), Snackbar.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }

            hitUnfilteredApi(handleToUse)

            val bundle = Bundle().apply {
                putString(FROM_FILTER, "fromDisCart")
            }
            findNavController().navigate(R.id.action_filterFragment_to_productListFragment, bundle)
            findNavController().popBackStack(R.id.filterFragment, true)
        }

        binding.brandLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putStringArrayList("BRAND_LIST", ArrayList(brandList))
            }
            findNavController().navigate(R.id.action_filterFragment_to_brandFragment, bundle)
        }
    }

    fun hitApi(productId: String) {
        binding.applyTv.setOnClickListener {
            val selectedMinPrice = binding.seekBarId.values[0].toInt()
            val selectedMaxPrice = binding.seekBarId.values[1].toInt()
            viewModel.selectedColors = selectedColors.toMutableList()
            viewModel.selectedSizes = selectedSizes.toMutableList()
            viewModel.selectedCategories = selectedCategories.toMutableList()
            viewModel.selectedBrands = brandList.toMutableList()
            viewModel.minPrice = selectedMinPrice.toFloat()
            viewModel.maxPrice = selectedMaxPrice.toFloat()

            val filters = ViewAllProductRequest.Filters(
                color = selectedColors,
                size = selectedSizes,
                minPrice = selectedMinPrice,
                maxPrice = selectedMaxPrice
            )
            val request = ViewAllProductRequest(
                handle = productId,
                filters = filters
            )

            homeViewModel.getViewAllData(token, request)

            val bundle = Bundle()
            bundle.putString(FROM_FILTER, "fromFilter")
            findNavController().navigate(R.id.action_filterFragment_to_productListFragment, bundle)
            findNavController().popBackStack(R.id.filterFragment, true)
        }
    }

    private fun setupColorRecyclerView(
        apiColors: List<String>,
        apiSizes: List<String>,
        apiGenderList: List<String>,
    ) {
        colorsList.clear()
        sizesList.clear()
        categoryList.clear()

        colorsList.addAll(apiColors.map { color ->
            val isSelected = selectedColors.contains(color.lowercase())
            DataString(color.uppercase(), isSelected)
        })

        sizesList.addAll(apiSizes.map { size ->
            val isSelected = selectedSizes.contains(mapSizeTitleToValue(size))
            DataString(size.uppercase(), isSelected)
        })

        categoryList.addAll(apiGenderList.map { gender ->
            val isSelected = selectedCategories.contains(gender.lowercase())
            DataString(gender.uppercase(), isSelected)
        })

        // Set up adapters
        colorsAdapter = ColorsListAdapter(
            colorsList,
            onColorClick = { selected ->
                selectedColors.clear()
                selectedColors.addAll(selected.map { it.title.lowercase() })
                Log.d("SelectedColors", selectedColors.toString())
            }
        )
        binding.colorsRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.colorsRecycler.adapter = colorsAdapter

        sizesAdapter = SizesAdapter(
            sizesList,
            onColorClick = { selected ->
                selectedSizes.clear()
                selectedSizes.addAll(selected.map { mapSizeTitleToValue(it.title) })
                Log.d("SelectedSizes", selectedSizes.toString())
            }
        )
        binding.sizesRecycler.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.sizesRecycler.adapter = sizesAdapter

        categoryListAdapter = CategoryListAdapter(
            categoryList,
            onColorClick = { selected ->
                selectedCategories.clear()
                selectedCategories.addAll(selected.map { it.title.lowercase() })
                Log.d("SelectedCategories", selectedCategories.toString())
            }
        )
        binding.categoryRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.categoryRecycler.adapter = categoryListAdapter
    }

    private fun setupClickListeners() {
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun mapSizeTitleToValue(title: String): String {
        return when (title.uppercase()) {
            "XS" -> "extra_small"
            "S" -> "small"
            "M" -> "medium"
            "L" -> "large"
            "XL" -> "extra_large"
            else -> title.lowercase()
        }
    }

    private fun setObserver() {

        viewModel.getFilterData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    if (statusCode == 200) {
                        val key = it.data.payload.Key

                        val colorList = key.color
                        val sizeList = key.size
                        val genderList = key.gender
                        val brandList: MutableList<String> = key.brand.toMutableList()
                        this.brandList = brandList
                        setupColorRecyclerView(colorList, sizeList, genderList)
                    } else {
                        Log.e("TAG", "Sorting API returned non-200")
                    }
                }

                Status.LOADING -> {
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.getViewAllLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.status // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {

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
    }

    fun hitUnfilteredApi(productId: String) {
        val filters = ViewAllProductRequest.Filters(
            color = emptyList(),
            size = emptyList(),
            minPrice = 0,
            maxPrice = 0
        )
        val request = ViewAllProductRequest(
            handle = productId,
            filters = filters
        )
        homeViewModel.getViewAllData(token, request)
    }
}