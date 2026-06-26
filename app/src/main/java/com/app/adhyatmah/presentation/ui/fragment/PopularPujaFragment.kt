package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.databinding.FragmentPopularPujaBinding
import com.app.adhyatmah.domain.model.PopularPooja
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.PopularPujasGridAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.common_utils.ProcessDialog

class PopularPujaFragment : BaseFragment<FragmentPopularPujaBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val popularPoojaList: ArrayList<PopularPooja> = ArrayList()
    private lateinit var popularPujasGridAdapter: PopularPujasGridAdapter

    override fun setLayout(): Int = R.layout.fragment_popular_puja

    override fun initView(savedInstanceState: Bundle?) {
        setObserver()
        ProcessDialog.showDialog(requireActivity(), true)
        homeViewModel.popularPujaListApi()

        binding.ivBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun setObserver() {
        homeViewModel.getPopularPujaListLiveData().observe(viewLifecycleOwner) { res ->
            res.data?.data?.let { list ->
                popularPoojaList.clear()
                popularPoojaList.addAll(list)
                popularPujasGridAdapter =
                    PopularPujasGridAdapter(popularPoojaList) { selectedPuja ->
                        UserPreference.selectedType = "Service"
                        UserPreference.search = selectedPuja.name ?: ""
                        (requireActivity() as? MainActivity)?.panditJiFromPopularPuja = true
                        (requireActivity() as? MainActivity)?.switchToPanditJiTab()
                    }
                binding.rvPopularPuja.adapter = popularPujasGridAdapter
                ProcessDialog.dismissDialog(true)
            }
        }
    }
}