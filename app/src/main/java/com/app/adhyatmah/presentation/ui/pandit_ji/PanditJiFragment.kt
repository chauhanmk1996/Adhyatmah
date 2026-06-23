package com.app.adhyatmah.presentation.ui.pandit_ji

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentPanditJiBinding
import com.app.adhyatmah.domain.model.create_booking.PanditJiDetails
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.Vendor
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.PanditJiAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.PanditListViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toDrawable
import com.app.adhyatmah.domain.model.PopularPooja
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.PopularPujasGridAdapter
import com.app.adhyatmah.presentation.ui.bottom_sheet.SignUpRequiredBottomSheetFragment
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel

class PanditJiFragment : BaseFragment<FragmentPanditJiBinding>() {

    private var selectedType: String = "PanditJi"
    private lateinit var panditJiAdapter: PanditJiAdapter
    private val viewmodel by activityViewModels<PanditListViewModel>()
    private val currentPanditList = mutableListOf<Vendor>()
    private var searchJob: Job? = null
    private var poojaSelectFromHomeName: String = ""
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val popularPoojaList: ArrayList<PopularPooja> = ArrayList()
    private lateinit var popularPujasGridAdapter: PopularPujasGridAdapter

    override fun setLayout(): Int = R.layout.fragment_pandit_ji

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        homeViewModel.popularPujaListApi()
    }

    override fun initView(savedInstanceState: Bundle?) {
        spinnerSet()
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""

                if (query.isEmpty()) {
                    searchJob?.cancel()
                    showFullList()
                    return
                }

                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300)
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
            setAdapter(currentPanditList)
            binding.recSearch.visibility = View.VISIBLE
            binding.noResult.visibility = View.GONE
        } else {
            viewmodel.hitPanditListApi()
        }
    }

    private var ignoreNextSpinnerSelection = false

    private fun setSpinnerOptions(options: List<String>, panditJiList: List<Vendor>) {
        val spinnerItems = mutableListOf<String>()
        spinnerItems.add(getString(R.string.select))
        spinnerItems.addAll(options)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            spinnerItems
        )
        binding.mySpinner.adapter = adapter

        currentPanditList.clear()
        currentPanditList.addAll(panditJiList)

        // Set initial selection to placeholder without triggering listener
        binding.mySpinner.setSelection(0, false)

        // Delay setting the listener to avoid premature calls during spinner initialization
        binding.mySpinner.post {
            binding.mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
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

                    val actual = spinnerItems[position] // "PanditJi" or "Service"
                    selectedType = actual
                    Log.d("SpinnerDebug", "Selected type: $selectedType")

                    if (selectedType == "Service") {
                        binding.searchView.setText("")
                        Log.d("SpinnerDebug", "Opening pooja selection dialog")
                        openPoojaSelectionDialog()
                        ignoreNextSpinnerSelection = true
                        binding.mySpinner.setSelection(0, false)
                    } else {
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

        ignoreNextSpinnerSelection = false

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "searchData",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.isEmpty) return@setFragmentResultListener
            val type = bundle.getString("selectedType")
            val search = bundle.getString("search")
            poojaSelectFromHomeName = search ?: ""

            if (!type.isNullOrEmpty()) {
                selectedType = type
            }

            if (!search.isNullOrEmpty()) {
                for (i in 0 until adapter.count) {
                    if (adapter.getItem(i).toString() == search) {
                        binding.mySpinner.setSelection(i)
                        break
                    }
                }
                binding.searchView.setText(search)
            }
        }

        setAdapter(panditJiList)
    }

    private fun openPoojaSelectionDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_multiple_pooja)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val rvPopularPuja = dialog.findViewById<RecyclerView>(R.id.rv_popular_puja)
        popularPujasGridAdapter =
            PopularPujasGridAdapter(popularPoojaList) { selectedPuja ->
                (requireActivity() as? MainActivity)?.panditJiFromPopularPuja = false
                poojaSelectFromHomeName = selectedPuja.name ?: ""
                binding.searchView.setText(selectedPuja.name ?: "")
                performSearch(selectedPuja.name ?: "")
                dialog.dismiss()
            }
        rvPopularPuja.adapter = popularPujasGridAdapter
        dialog.show()
    }

    private fun setAdapter(panditJiList: List<Vendor>) {
        panditJiAdapter = PanditJiAdapter(panditJiList.toMutableList()) { data ->
            UserPreference.panditJiDetails = PanditJiDetails(
                id = data.id,
                image = data.image?.url ?: "",
                firstName = data.firstName ?: "",
                lastName = data.lastName ?: "",
                city = data.city ?: "",
                experience = data.experience ?: "",
                about = data.about ?: "",
                seoContent = data.seoContent,
                gotra = data.gotra ?: "",
                verified = data.verified ?: false,
                trusted = data.trusted ?: false,
                address = data.address ?: "",
                panditLanguage = data.language,
                poojaSelectFromHomeName = poojaSelectFromHomeName
            )
            if (Preferences.getStringPreference(requireContext(), IS_LOGIN) == "1") {
                if ((data.services?.size ?: 0) > 0) {
                    findNavController().navigate(R.id.bookingDetailsFragment)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.no_service_available),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                signupRequired(getString(R.string.sign_up_required_to_book_a_pandit_ji))
            }
        }
        binding.recSearch.adapter = panditJiAdapter
        binding.recSearch.visibility = View.VISIBLE
        binding.noResult.visibility = if (panditJiList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun signupRequired(message: String) {
        val bottomSheet =
            SignUpRequiredBottomSheetFragment(message) {
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                requireActivity().startActivity(intent)
            }
        bottomSheet.show(parentFragmentManager, "SignUpRequiredBottomSheetFragment")
    }

    private fun setObserver() {
        viewmodel.getPanditListLiveData().observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    val code = res.data?.code
                    if (code == 200) {
                        val vendors = res.data.payload.vendors
                        currentPanditList.clear()
                        currentPanditList.addAll(vendors)

                        binding.searchView.text?.toString()?.trim() ?: ""
                        val spinnerItems = listOf("PanditJi", "Service")
                        if (binding.mySpinner.adapter == null) {
                            setSpinnerOptions(spinnerItems, vendors)
                        } else {
                            setAdapter(vendors)
                        }
                    } else {
                        setAdapter(emptyList())
                    }
                }

                Status.ERROR -> {
                    Snackbar.make(
                        requireView(),
                        res.message ?: getString(R.string.something_went_wrong),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        homeViewModel.getPopularPujaListLiveData().observe(viewLifecycleOwner) { res ->
            res.data?.data?.let { list ->
                popularPoojaList.clear()
                popularPoojaList.addAll(list)
            }
        }
    }
}