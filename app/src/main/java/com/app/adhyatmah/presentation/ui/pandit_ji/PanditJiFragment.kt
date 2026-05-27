package com.app.adhyatmah.presentation.ui.pandit_ji

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentPanditJiBinding
import com.app.adhyatmah.domain.model.create_booking.PanditjiBookingRequest
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.Vendor
import com.app.adhyatmah.domain.model.search_list_response.search_list_request.SearchListRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.PanditJiAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.MultiplePoojaAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.PanditListViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PanditJiFragment : BaseFragment<FragmentPanditJiBinding>() {

    private var selectedType: String = "PanditJi" // Default selection
    private lateinit var panditJiAdapter: PanditJiAdapter
    private lateinit var multiplePoojaAdapter: MultiplePoojaAdapter
    private val viewmodel by activityViewModels<PanditListViewModel>()

    private val currentPanditList = mutableListOf<Vendor>()
    private var searchJob: Job? = null
    private val SEARCH_DEBOUNCE_MS = 300L

    override fun setLayout(): Int = R.layout.fragment_pandit_ji

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    override fun initView(savedInstanceState: Bundle?) {
        spinnerSet()
        binding.backImg.setOnClickListener { findNavController().navigateUp() }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""

                // if blank -> show cached full list
                if (query.isEmpty()) {
                    searchJob?.cancel()
                    showFullList()
                    return
                }

                // Debounce API calls
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(SEARCH_DEBOUNCE_MS)
                    performSearch(query)
                }
            }
        })
    }

    private fun spinnerSet() {
        viewmodel.hitPanditListApi()
    }

    private fun performSearch(query: String) {
        if (selectedType == "PanditJi") {
            viewmodel.hitPanditListApi(name = query)
        } else {
            viewmodel.hitPanditListApi(serviceName = query)
        }
    }

    private fun showFullList() {
        if (currentPanditList.isNotEmpty()) {
            setAdapter(selectedType, currentPanditList)
            binding.recSearch.visibility = View.VISIBLE
            binding.noResult.visibility = View.GONE
        } else {
            viewmodel.hitPanditListApi()
        }
    }

    private var ignoreNextSpinnerSelection = false

    private fun setSpinnerOptions(options: List<String>, panditjiList: List<Vendor>) {
        // Prepend a placeholder so selection can be toggled
        val spinnerItems = mutableListOf<String>()
        spinnerItems.add(getString(R.string.select)) // index 0 placeholder
        spinnerItems.addAll(options) // e.g. "PanditJi", "Service"

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerItems)
        binding.mySpinner.adapter = adapter

        currentPanditList.clear()
        currentPanditList.addAll(panditjiList)

        // Set initial selection to placeholder without triggering listener
        binding.mySpinner.setSelection(0, false)

        // Delay setting the listener to avoid premature calls during spinner initialization
        binding.mySpinner.post {
            binding.mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    Log.d("SpinnerDebug", "onItemSelected called: position=$position, value=${spinnerItems[position]}, ignoreNextSpinnerSelection=$ignoreNextSpinnerSelection")

                    // Skip if this is a programmatic selection
                    if (ignoreNextSpinnerSelection) {
                        ignoreNextSpinnerSelection = false
                        Log.d("SpinnerDebug", "Ignored programmatic selection")
                        return
                    }

                    // position 0 -> placeholder; treat as no selection
                    if (position == 0) {
                        selectedType = "PanditJi"
                        binding.searchView.setText("")
                        searchJob?.cancel()
                        showFullList()
                        Log.d("SpinnerDebug", "Placeholder selected, resetting to PanditJi")
                        return
                    }

                    // Actual selections are shifted by +1 because of the placeholder
                    val actual = spinnerItems[position] // "PanditJi" or "Service"
                    selectedType = actual
                    Log.d("SpinnerDebug", "Selected type: $selectedType")

                    if (selectedType == "Service") {
                        // Clear the search and open pooja dialog
                        binding.searchView.setText("")
                        Log.d("SpinnerDebug", "Opening pooja selection dialog")
                        openPoojaSelectionDialog()
                        // Reset spinner to placeholder to allow re-triggering
                        ignoreNextSpinnerSelection = true
                        binding.mySpinner.setSelection(0, false)
                        Log.d("SpinnerDebug", "Reset spinner to placeholder after Service selection")
                    } else {
                        // PanditJi selected -> clear search and show full list
                        binding.searchView.setText("")
                        searchJob?.cancel()
                        showFullList()
                        Log.d("SpinnerDebug", "Showing full PanditJi list")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Log.d("SpinnerDebug", "onNothingSelected called")
                }
            }
        }

        // Reset ignore flag after initial setup
        ignoreNextSpinnerSelection = false

        // Show initial adapter content
        setAdapter(selectedType, panditjiList)
    }
    /** Dialog showing list of poojas */
    private fun openPoojaSelectionDialog() {
        val poojaList = poojaList

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_multiple_pooja)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val recycler = dialog.findViewById<RecyclerView>(R.id.recMultiplePooja)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        multiplePoojaAdapter = MultiplePoojaAdapter(poojaList) { _, selectedItems ->
            if (selectedItems.isNotEmpty()) {
                val selectedPooja = selectedItems.first()
                dialog.dismiss()

                // set text and hit API
                binding.searchView.setText(selectedPooja)
                performSearch(selectedPooja)
            }
        }
        recycler.adapter = multiplePoojaAdapter

        dialog.show()
    }

    private fun setAdapter(selectedType: String, panditjiList: List<Vendor>) {
        panditJiAdapter = PanditJiAdapter(panditjiList.toMutableList(), { imgClickPosition ->
            Log.d("TAG", "Pandit Image Clicked at position: $imgClickPosition")
        }, { data ->
            UserPreference.panditjiBookingRequest = PanditjiBookingRequest()
            UserPreference.panditjiBookingRequest.apply {
                address = data.address
                vendorId = data.id
                firstName = data.firstName
                lastName = data.lastName
                image = data.image?.url
                bookingId = ""
                about = data.about
            }
            if(Preferences.getStringPreference(requireContext(), IS_LOGIN)=="1"){
                if ((data.services?.size ?: 0) > 0) {
                    findNavController().navigate(R.id.bookingDetailsFragment)
//                    findNavController().navigate(R.id.selectLanguageFragment)

                } else {
                    Toast.makeText(requireActivity(), "No Service Available!", Toast.LENGTH_SHORT).show()
                }
            }else{
                showLoginPrompt()
            }
        })

        binding.recSearch.adapter = panditJiAdapter
        binding.btnNext.visibility = View.GONE
        binding.recSearch.visibility = View.VISIBLE
        binding.noResult.visibility = if (panditjiList.isEmpty()) View.VISIBLE else View.GONE
    }
    private fun showLoginPrompt() {
        var dialog: AlertDialog? = null
        dialog=  CommonUtils.showCustomAlertDialog(
            requireActivity() ,
            "Sign Up Required",
            "Sign up required to Book a Pandit Ji.",
            positiveButtonText = "Sign up",
            negativeButtonText = "Cancel",
            positiveButtonAction = {
                dialog?.dismiss()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                val bundle = Bundle()
                requireActivity().startActivity(intent)            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }
    private fun setObserver() {
        viewmodel.getPanditListLiveData().observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val code = res.data?.code
                    if (code == 200) {
                        val vendors = res.data.payload.vendors ?: emptyList()
                        currentPanditList.clear()
                        currentPanditList.addAll(vendors)

                        val currentQuery = binding.searchView.text?.toString()?.trim() ?: ""
                        val spinnerItems = listOf("PanditJi", "Service")
                        if (binding.mySpinner.adapter == null) {
                            setSpinnerOptions(spinnerItems, vendors)
                        } else {
                            setAdapter(selectedType, vendors)
                        }
                    } else {
                        ProcessDialog.dismissDialog(true)
                        setAdapter(selectedType, emptyList())
                    }
                }
                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), res.message ?: "Something went wrong", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}