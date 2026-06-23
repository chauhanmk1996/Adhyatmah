package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.ADDRESS_ID
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.databinding.FragmentAddAddressBinding
import com.app.adhyatmah.domain.model.profile.add_address.AddAddressRequest
import com.app.adhyatmah.domain.model.profile.add_address.AddAddressRequest.Address
import com.app.adhyatmah.domain.model.profile.manage_address.Addresse
import com.app.adhyatmah.presentation.ui.viewmodel.ProfileViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.google.android.material.snackbar.Snackbar

class AddAddressFragment : BaseFragment<FragmentAddAddressBinding>() {
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private var isEditMode = false
    private var addressId = ""
    var type = ""
    var from = ""
    var selectedState: String = ""
    private var statesList: List<String> = emptyList()

    override fun setLayout(): Int {
        return R.layout.fragment_add_address
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserve()
    }

    override fun initView(savedInstanceState: Bundle?) {
        val addressData = arguments?.getParcelable<Addresse>("address_data")
        type = arguments?.getString("type").toString()
        from = arguments?.getString("from").toString()
        addressId = addressData?.id.toString()
        Log.d("TAG", "initView: $addressId")
        val addressType = arguments?.getString(TYPE)
        if (addressType == "edit_address" && addressData != null) {
            isEditMode = true
            setData(addressData)
        }

        hitAPi()
        click()
    }

    fun hitAPi() {
        binding.continueBtn.setOnClickListener {
            if (validateInput()) {
                if (isEditMode) {
                    hitEditAddressAPI()
                } else {
                    hitAddAddressAPI()
                }
            }
        }
        profileViewModel.hitIndianStateApi()
    }

    fun click() {
        binding.scrollView.post {
            binding.continueBtn.bottom.let { binding.scrollView.smoothScrollTo(0, it) }
        }

        binding.backImg.setOnClickListener {
            findNavController().navigateUp()
        }
        keyBoardClose()
    }

    private fun setSpinnerOptions(options: List<String>) {
        val listWithPlaceholder = mutableListOf(getString(R.string.select_state))
        listWithPlaceholder.addAll(options)
        statesList = listWithPlaceholder

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            statesList
        )
        binding.stateEt.adapter = adapter

        if (isEditMode) {
            val index =
                listWithPlaceholder.indexOfFirst { it.equals(selectedState, ignoreCase = true) }
            if (index != -1) {
                binding.stateEt.setSelection(index)
            }
        } else {
            binding.stateEt.setSelection(0)
            selectedState = ""
        }

        binding.stateEt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectedState = if (position != 0) {
                    listWithPlaceholder[position]
                } else {
                    ""
                }
                Log.d("TAG", "Selected State: $selectedState")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun setData(address: Addresse) {
        val nameParts = address.name?.split(" ") ?: listOf("", "")
        binding.firstNameEt.setText(nameParts.firstOrNull() ?: "")
        binding.lastNameEt.setText(nameParts.getOrNull(1) ?: "")
        binding.localityEt.setText(address.address1 ?: "")
        binding.landmarkEt.setText(address.address2 ?: "")
        binding.cityEt.setText(address.city ?: "")
        binding.countryEt.setText(address.country ?: "United States")
        binding.pinCodeEt.setText(address.zip ?: "")
        binding.phonenumberInput.setText(address.phone ?: "")
        selectedState = address.province ?: ""
    }

    fun hitEditAddressAPI() {
        val token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN)
        val request = AddAddressRequest()
        val address = Address()
        address.firstName = binding.firstNameEt.getString()
        address.lastName = binding.lastNameEt.getString()
        address.address1 = binding.localityEt.getString()
        address.address2 = binding.landmarkEt.getString()
        address.city = binding.cityEt.getString()
        address.province = selectedState
        address.country = binding.countryEt.getString()
        address.zip = binding.pinCodeEt.getString()
        address.phone = binding.phonenumberInput.getString()
        request.accessToken = token
        request.address = address
        request.addressId = addressId
        profileViewModel.getEditAddressData(request)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun keyBoardClose() {
        binding.scrollView.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val currentFocusedView = requireActivity().currentFocus
                if (currentFocusedView is EditText) {
                    val imm =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
                }
            }
            false
        }
    }

    fun hitAddAddressAPI() {
        val token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN)
        val request = AddAddressRequest()
        val address = Address()
        address.firstName = binding.firstNameEt.getString()
        address.lastName = binding.lastNameEt.getString()
        address.address1 = binding.localityEt.getString()
        address.address2 = binding.landmarkEt.getString()
        address.city = binding.cityEt.getString()
        address.province = selectedState
        address.country = binding.countryEt.getString()
        address.zip = binding.pinCodeEt.getString()
        address.phone = binding.phonenumberInput.getString()
        request.accessToken = token
        request.address = address
        profileViewModel.getCreateAddressData(request)
    }

    private fun validateInput(): Boolean {
        val firstName = binding.firstNameEt.getString()
        val lastName = binding.lastNameEt.getString()
        val phone = binding.phonenumberInput.getString()
        val locality = binding.localityEt.getString()
        val city = binding.cityEt.getString()
        val pinCode = binding.pinCodeEt.getString()
        val country = binding.countryEt.getString()

        if (firstName.isEmpty()) {
            binding.firstNameEt.error = getString(R.string.enter_first_name)
            return false
        }

        if (lastName.isEmpty()) {
            binding.lastNameEt.error = getString(R.string.enter_last_name)
            return false
        }

        if (phone.isEmpty() || phone.length < 10) {
            binding.phonenumberInput.error = getString(R.string.enter_a_valid_phone_number)
            return false
        }

        if (locality.isEmpty()) {
            binding.localityEt.error = getString(R.string.locality_is_required)
            return false
        }

        if (pinCode.isEmpty() || pinCode.length < 3) {
            binding.pinCodeEt.error = getString(R.string.enter_a_valid_pin_code)
            return false
        }

        if (city.isEmpty()) {
            binding.cityEt.error = getString(R.string.city_is_required)
            return false
        }

        if (selectedState.isEmpty() || selectedState == getString(R.string.select_state)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_select_a_state), Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (country.isEmpty()) {
            binding.countryEt.error = getString(R.string.state_is_required)
            return false
        }
        return true
    }

    private fun setObserve() {
        profileViewModel.getCreateAddressRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val address = it.data.payload.address
                            val id =
                                address.id
                            Toast.makeText(
                                requireActivity(),
                                it.data.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            val bundle = Bundle().apply {
                                putString(ADDRESS_ID, id)
                            }

                            if (type == "1") {
                                findNavController().popBackStack()
                            } else if (isEditMode) {
                                findNavController().popBackStack()
                            } else {
                                if (from == "bookPanditji") {
                                    findNavController().popBackStack()
                                } else {
                                    findNavController().navigate(
                                        R.id.action_addAddressFragment_to_paymentMethodFragment,
                                        bundle
                                    )
                                }
                            }

                            ProcessDialog.dismissDialog(true)
                        }

                        401 -> {
                            Toast.makeText(requireActivity(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                            Log.e("TAG", "Unauthorized access $it")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.data?.message}", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

        profileViewModel.getIndianStateRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.states
                            setSpinnerOptions(data)
                            Log.d("TAG", "setObserve: $data")
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