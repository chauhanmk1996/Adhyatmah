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
    private lateinit var brandList: MutableList<DataString>
    lateinit var adapter: BrandAdapter

    val selectedBrands = mutableListOf<String>()

    override fun setLayout(): Int {
        return R.layout.fragment_brand
    }

    override fun initView(savedInstanceState: Bundle?) {
        brandListAPi = arguments?.getStringArrayList("BRAND_LIST") ?: emptyList()
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        brandList = brandListAPi.map { DataString(it.uppercase(), false) }.toMutableList()

        setAdapter()
        binding.discardTv.setOnClickListener {
            brandList.forEach { it.isSelect = false }
            selectedBrands.clear()
            adapter.notifyDataSetChanged()
        }

        binding.applyTv.setOnClickListener {
            val bundle = Bundle()
            bundle.putStringArray("SELECTED_BRANDS", selectedBrands.toTypedArray())
            bundle.putString(TYPE, "BRAND")
            Log.d("TAG", "initView: ${selectedBrands}")
            findNavController().navigate(R.id.action_brandFragment_to_filterFragment, bundle)
        }
    }

    fun setAdapter() {
        adapter = BrandAdapter(
            brandList,
            onBrandClick = { selected ->
                selected.forEach { brand ->
                    if (brand.isSelect) {
                        if (!selectedBrands.contains(brand.title.lowercase())) {
                            selectedBrands.add(brand.title.lowercase())
                        }
                    } else {
                        selectedBrands.remove(brand.title.lowercase())
                    }
                }
                adapter.notifyDataSetChanged()
            }
        )

        binding.brandRecyclerId.adapter = adapter
    }
}

