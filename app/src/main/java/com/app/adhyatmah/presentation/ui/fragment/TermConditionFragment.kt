package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentTermConditionBinding
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class  TermConditionFragment : BaseFragment<FragmentTermConditionBinding>() {
    private val viewModel by activityViewModels<FilterViewModel>()
    var type =""

    override fun setLayout(): Int {
        return R.layout.fragment_term_condition
    }

    override fun initView(savedInstanceState: Bundle?) {
        type = arguments?.getString("terms") ?: ""
        when (type) {
            "1", "2" -> {
                binding.title.text = getString(R.string.terms_of_services)
            }
            else -> {
                binding.title.text = getString(R.string.privacy_policy)
            }
        }

        viewModel.hitPrivacyApi()
        setObserver()

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

    }

    private fun setObserver() {
        viewModel.getPrivacyList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            val term = data.termsOfService.body
                            val privacy = data.privacyPolicy.body

                            when (type) {
                                "1", "2" -> {
                                    binding.des.text = Html.fromHtml(term, Html.FROM_HTML_MODE_LEGACY)
                                }
                                else -> {
                                    binding.des.text = Html.fromHtml(privacy, Html.FROM_HTML_MODE_LEGACY)
                                }
                            }
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