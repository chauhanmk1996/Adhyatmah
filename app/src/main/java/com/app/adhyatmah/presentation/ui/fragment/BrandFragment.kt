package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.DataString
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.databinding.FragmentBrandBinding
import com.app.adhyatmah.presentation.ui.adapter.BrandAdapter
import com.app.adhyatmah.utils.base.BaseFragment

class BrandFragment : BaseFragment<FragmentBrandBinding>() {

    private lateinit var brandListAPi: List<String>
    private lateinit var brandList: MutableList<DataString> // Use MutableList to update it dynamically
    lateinit var adapter: BrandAdapter

    val selectedBrands = mutableListOf<String>() // List to track selected brands

    override fun setLayout(): Int {
        return R.layout.fragment_brand
    }

    override fun initView(savedInstanceState: Bundle?) {
        // Receive brand list from the previous fragment (FilterFragment)
        brandListAPi = arguments?.getStringArrayList("BRAND_LIST") ?: emptyList()
         binding.backButton.setOnClickListener {
             findNavController().navigateUp()
         }
        // Log the received brand list (for debugging)
        Log.d("BrandFragment", "Received brand list: $brandListAPi")

        // Initialize the list of DataString objects with the brand list
        brandList = brandListAPi.map { DataString(it.uppercase(), false) }.toMutableList()

        // Set the adapter with the brand list
        setAdapter()
        binding.discardTv.setOnClickListener {
            brandList.forEach { it.isSelect = false }

            // Clear the selectedBrands list
            selectedBrands.clear()
            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged()

        }
        binding.applyTv.setOnClickListener {
            var bundle = Bundle()

            bundle.putStringArray("SELECTED_BRANDS", selectedBrands.toTypedArray())
             bundle.putString(TYPE,"BRAND")
            Log.d("TAG", "initView: ${selectedBrands}")

            findNavController().navigate(R.id.action_brandFragment_to_filterFragment,bundle)

            // Log the selected brands for debugging
        }

    }

    fun setAdapter() {
        adapter = BrandAdapter(
            brandList,
            onBrandClick = { selected ->
                // Update the selectedBrands list based on the user's selection/deselection
                selected.forEach { brand ->
                    if (brand.isSelect) {
                        // If selected, add the brand to selectedBrands (if not already selected)
                        if (!selectedBrands.contains(brand.title.lowercase())) {
                            selectedBrands.add(brand.title.lowercase())
                        }
                    } else {
                        // If deselected, remove the brand from selectedBrands
                        selectedBrands.remove(brand.title.lowercase())
                    }
                }

                // Log the selected brands (for debugging)
                Log.d("BrandFragment", "Selected brands after click: $selectedBrands")

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged()
            }
        )

        // Set the RecyclerView adapter
        binding.brandRecyclerId.adapter = adapter
    }
       /* adapter = BrandAdapter(
            brandList,
            onBrandClick = { selected ->
                // Clear the selectedBrands list and add the newly selected brands
                selectedBrands.clear()
                selectedBrands.addAll(selected.map { it.title.lowercase() })

                // Log the selected brands (for debugging)
                Log.d("BrandFragment", "Selected brands: $selectedBrands")
    }
        )

        // Set the RecyclerView layout and adapter
       // binding.brandRecyclerId.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.brandRecyclerId.adapter = adapter*/
    }

