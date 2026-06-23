package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.ADDRESS_ID
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentMangeAddressBinding
import com.app.adhyatmah.domain.model.delete_address.request_Address.DeleteAddressRequest
import com.app.adhyatmah.domain.model.profile.manage_address.Addresse
import com.app.adhyatmah.domain.model.profile.manage_address.ManageAddressRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.adapter.ManageAddressAdapter
import com.app.adhyatmah.presentation.ui.bottom_sheet.SignUpRequiredBottomSheetFragment
import com.app.adhyatmah.presentation.ui.viewmodel.ProfileViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class MangeAddressFragment : BaseFragment<FragmentMangeAddressBinding>() {

    private val profileViewModel by activityViewModels<ProfileViewModel>()
    lateinit var manageAddListAdapter: ManageAddressAdapter
    var token = ""
    var from = ""

    override fun setLayout(): Int {
        return R.layout.fragment_mange_address
    }

    override fun initView(savedInstanceState: Bundle?) {
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        from = arguments?.getString("from").toString()
        fetchAddressList()
        setObserver()
    }

    override fun onResume() {
        super.onResume()
        fetchAddressList()
        setObserver()

    }

    fun setAdapter(addresses: MutableList<Addresse>, cardId: String) {
        manageAddListAdapter = ManageAddressAdapter(addresses) { id, address_data, type, data ->
            if (type == "mainLayoutClick") {
                if (from == "bookPanditji" || from == "home") {
                    val address = data.address1 + ", " + data.city + ", " + data.province + ", " + data.country + " - " + data.zip
                    UserPreference.savedAddressId = data.id?:""
                    UserPreference.savedAddress = address
                    findNavController().popBackStack()
                } else {
                    val bundle = Bundle().apply {
                        putString(CART_ID, cardId)
                        putString(ADDRESS_ID, id)
                        putString(
                            "address",
                            address_data.name + " " + address_data.address1 + " " + address_data.address2
                        )
                    }
                    findNavController().navigate(
                        R.id.action_mangeAddressFragment_to_paymentMethodFragment,
                        bundle
                    )
                }
            } else {
                showActionPrompt(id, type, address_data)
            }
        }
        binding.recyclerView.adapter = manageAddListAdapter
    }

    private fun showActionPrompt(id: String, type: String, addresses: Addresse) {
        Log.d("TAG", "showActionPrompt1: $addresses")
        var dialog: AlertDialog? = null
        val title = when (type) {
            "delete" -> getString(R.string.delete_address)
            "edit" -> getString(R.string.edit_address)
            else -> ""
        }
        val message = when (type) {
            "delete" -> getString(R.string.are_you_sure_you_want_to_delete_the_saved_address)
            "edit" -> getString(R.string.do_you_want_to_edit_this_address)
            else -> ""
        }
        val positiveText = when (type) {
            "delete" -> getString(R.string.delete)
            "edit" -> getString(R.string.edit)
            else -> ""
        }

        dialog = CommonUtils.showCustomAlertDialog(
            requireActivity(),
            title,
            message,
            positiveButtonText = positiveText,
            negativeButtonText = getString(R.string.cancel),
            positiveButtonAction = {
                dialog?.dismiss()
                when (type) {
                    "delete" -> {
                        deleteAddress(id)
                    }

                    "edit" -> {
                        navigateToEditAddress(addresses)
                    }
                }
            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }

    fun deleteAddress(id: String) {
        val token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN)
        val request = DeleteAddressRequest()
        request.addressId = id
        request.accessToken = token
        profileViewModel.hitDeleteAddressApi(request)
    }

    fun navigateToEditAddress(addresses: Addresse) {
        val bundle = Bundle()
        Log.d("TAG", "navigateToEditAddress: $addresses")
        bundle.putParcelable("address_data", addresses)
        bundle.putString(TYPE, "edit_address")
        bundle.putString("from", "bookPanditji")
        findNavController().navigate(R.id.action_mangeAddressFragment_to_addAddressFragment, bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backImg.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.addBtn.setOnClickListener {
            if (token.isEmpty()) {
                signupRequired(getString(R.string.please_sign_up_required_to_add_address_and_see_address))
            } else {
                val bundle = Bundle()
                bundle.putString("type", "1")
                bundle.putString("from", "bookPanditji")
                findNavController().navigate(
                    R.id.action_mangeAddressFragment_to_addAddressFragment,
                    bundle
                )
            }
        }
    }

    fun fetchAddressList() {
        if (token.isEmpty()) {
            signupRequired(getString(R.string.please_sign_up_required_to_add_address_and_see_address))
            return
        }
        val request = ManageAddressRequest()
        request.accessToken = token
        profileViewModel.getAddressData(request)
    }

    private fun signupRequired(message:String) {
        val bottomSheet =
            SignUpRequiredBottomSheetFragment(message) {
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                requireActivity().startActivity(intent)
            }
        bottomSheet.show(parentFragmentManager, "SignUpRequiredBottomSheetFragment")
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setObserver() {
        profileViewModel.getCustomerAddressRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.addresses

                            if (data.isEmpty()) {
                                binding.addresstext.visibility = View.VISIBLE

                            } else {
                                val cardId = arguments?.getString(CART_ID)
                                binding.addresstext.visibility = View.GONE
                                setAdapter(data, cardId ?: "")
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
        profileViewModel.getDeleteAddressRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.message
                            Toast.makeText(requireContext(), data, Toast.LENGTH_SHORT).show()
                            fetchAddressList()
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