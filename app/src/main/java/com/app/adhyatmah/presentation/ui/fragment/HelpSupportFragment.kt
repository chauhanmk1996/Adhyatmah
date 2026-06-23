package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentHelpSupportBinding
import com.app.adhyatmah.domain.model.faq.Payload
import com.app.adhyatmah.presentation.ui.adapter.AdapterFaq
import com.app.adhyatmah.presentation.ui.viewmodel.PaymentViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class HelpSupportFragment : BaseFragment<FragmentHelpSupportBinding>() {
    val viewModel by activityViewModels <PaymentViewModel>()

    lateinit var adapter :AdapterFaq
    override fun setLayout(): Int {
        return R.layout.fragment_help_support
    }

    override fun initView(savedInstanceState: Bundle?) {
        setObserver()
        viewModel.hitFAQApi("user")
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun setAdapter(data: List<Payload>) {
        adapter = AdapterFaq(data)
        binding.faqRecycler.adapter = adapter
    }

    private fun setObserver(){
        viewModel.getFAQApiResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            setAdapter(data)
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