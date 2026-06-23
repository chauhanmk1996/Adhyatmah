package com.app.adhyatmah.presentation.ui.pandit_ji

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentSelectLanguageBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.PanditLanguageAdapter
import com.app.adhyatmah.utils.base.BaseFragment

class SelectLanguageFragment : BaseFragment<FragmentSelectLanguageBinding>() {

    override fun setLayout(): Int = R.layout.fragment_select_language
    private var panditLanguageList: ArrayList<String> = ArrayList()
    private lateinit var panditLanguageAdapter: PanditLanguageAdapter

    override fun initView(savedInstanceState: Bundle?) {
        setupRecycler()
        setupListeners()
    }

    private fun setupRecycler() {
        UserPreference.panditJiDetails.panditLanguage?.let { list ->
            panditLanguageList.addAll(list)
            panditLanguageAdapter = PanditLanguageAdapter(panditLanguageList) { language ->
                panditLanguageAdapter.selectionChange(language)
                UserPreference.panditjiBookingRequest.selectedLanguage = language
            }
            binding.rvLanguage.adapter = panditLanguageAdapter
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            if (UserPreference.panditjiBookingRequest.selectedLanguage?.isEmpty() == true) {
                Toast.makeText(
                    requireContext(),
                    "Please select at least one language",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(R.id.confirmBookingFragment)
            }
        }
    }

    /*private fun setObserver() {
        viewmodel.getLanguagesLiveData().observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val apiList = res.data?.payload?.languages ?: emptyList()
                    val finalList = apiList.ifEmpty { listOf("Hindi") }

                    languageAdapter = LanguageAdapter(finalList, selectedLanguages)
                    binding.rvLanguage.adapter = languageAdapter
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
    }*/
}