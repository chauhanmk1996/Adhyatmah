package com.app.adhyatmah.presentation.ui.fragment

import android.widget.Toast
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.app.adhyatmah.R
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import com.app.adhyatmah.databinding.FragmentPopularPujaBinding
import com.app.adhyatmah.domain.model.PopularPooja
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.PopularPujasGridAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel

class PopularPujaFragment : BaseFragment<FragmentPopularPujaBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val popularPoojaList: ArrayList<PopularPooja> = ArrayList()
    private lateinit var popularPujasGridAdapter: PopularPujasGridAdapter

    override fun setLayout(): Int = R.layout.fragment_popular_puja

    override fun initView(savedInstanceState: Bundle?) {
        setObserver()
        homeViewModel.homeDataApi()
    }

    private fun setObserver() {
        homeViewModel.getHomeDataApi().observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val code = res.data?.code
                    when (code) {
                        200 -> {
                            val payload = res.data.payload
                            payload?.services?.let { list ->
                                popularPoojaList.clear()
                                popularPoojaList.addAll(list)
                                popularPujasGridAdapter =
                                    PopularPujasGridAdapter(popularPoojaList) { selectedPuja ->
                                        (requireActivity() as? MainActivity)?.panditJiFromPopularPuja = true
                                        (requireActivity() as? MainActivity)?.switchToPanditJiTab(
                                            "Service",
                                            selectedPuja.name ?: ""
                                        )
                                    }
                                binding.rvPopularPuja.adapter = popularPujasGridAdapter
                            }
                        }
                        401 -> {
                            ProcessDialog.dismissDialog(true)
                            Toast.makeText(requireActivity(), res.data.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> {
                            ProcessDialog.dismissDialog(true)
                        }
                    }
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(
                        requireView(),
                        res.message ?: "Something went wrong",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}