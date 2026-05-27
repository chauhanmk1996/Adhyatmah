package com.app.adhyatmah.presentation.ui.pandit_ji

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentConfirmBookingBinding
import com.app.adhyatmah.databinding.FragmentSelectLanguageBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.SelectBookingLanguageAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.PanditListViewModel
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.SelectLanguageViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import java.util.Locale
import kotlin.getValue

class SelectLanguageFragment : BaseFragment<FragmentSelectLanguageBinding>() {

    private val viewmodel by activityViewModels<SelectLanguageViewModel>()
    private var selectedLanguages: MutableList<String> = mutableListOf()
    private var adapter: SelectBookingLanguageAdapter? = null

    override fun setLayout(): Int = R.layout.fragment_select_language

    override fun initView(savedInstanceState: Bundle?) {
        // Restore saved selections if any
        selectedLanguages.clear()
        selectedLanguages.addAll(UserPreference.panditjiBookingRequest.language?.toMutableList() ?: emptyList())

        viewmodel.hitGetLanguagesApi()

        binding.btnNext.setOnClickListener {
            if (selectedLanguages.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one language", Toast.LENGTH_SHORT).show()
            } else {
                // Save selection before navigation
                val lowercaseList = selectedLanguages.map { it.lowercase(Locale.ROOT) }
                UserPreference.panditjiBookingRequest.language = lowercaseList

                findNavController().navigate(R.id.confirmBookingFragment) // replace with your destination
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    private fun setObserver() {
        viewmodel.getLanguagesLiveData().observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val apiList = res.data?.payload?.languages ?: emptyList()
                    val finalList = if (apiList.isEmpty()) listOf("Hindi") else apiList

                    adapter = SelectBookingLanguageAdapter(finalList, selectedLanguages)
                    binding.rvServices.adapter = adapter
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), res.message ?: "Something went wrong", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
