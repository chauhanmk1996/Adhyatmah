package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.IS_PROFILE_DATA
import com.app.adhyatmah.data.preferences.PROFILE_IMG
import com.app.adhyatmah.databinding.FragmentProfileBinding
import com.app.adhyatmah.domain.model.profile.get_profile.Payload
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.adapter.ProfileAdapter
import com.app.adhyatmah.presentation.ui.bottom_sheet.LogoutBottomSheetFragment
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    var isLogin = ""
    override fun setLayout(): Int {
        return R.layout.fragment_profile
    }

    override fun onResume() {
        super.onResume()
        setProfileData()
    }

    override fun initView(savedInstanceState: Bundle?) {
        setProfileData()
        // Initialize RecyclerView
        setupRecyclerView()
    }

    fun setProfileData() {
        isLogin = Preferences.getStringPreference(requireContext(), IS_LOGIN).toString()
        val img = Preferences.getStringPreference(requireContext(), PROFILE_IMG).toString()
        val profileData =
            Preferences.getCustomModelPreference<Payload>(requireContext(), IS_PROFILE_DATA)
        Log.d("TAG", "indditView: $profileData $img")
        binding.userNameTv.text = if (profileData != null) {
            "${profileData.user?.firstName} ${profileData.user?.lastName}"
        } else {
            "User"
        }
        binding.userNumberTv.text = if (profileData != null) {
            profileData.user?.email
        } else {
            "Test@yopmail.com"
        }
        val imageUrl = img
        Log.d("TAG", "setProfileDataImgUrl : $imageUrl")

        if (imageUrl.isNullOrEmpty()) {
            binding.img.setImageResource(R.drawable.placeholder_icon)
        } else {
            Glide.with(requireContext())
                .load(imageUrl)
                .error(R.drawable.placeholder_icon) // if loading fails
                .into(binding.img)
        }
        binding.rightArrowImg.setOnClickListener {
            // Toast.makeText(requireContext(),"Under development",Toast.LENGTH_SHORT).show()
            if (isLogin == "1") {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            } else {
                showLoginPrompt()
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView() {
        // Sample data as a list of Pair<String, Int>
        val profileItems = mutableListOf(
            Pair(getString(R.string.wishlist), R.drawable.wishlist_icon),
            Pair(getString(R.string.my_orders), R.drawable.my_order_icon),
            Pair(getString(R.string.my_booking), R.drawable.term_icon),
            Pair(getString(R.string.manage_address), R.drawable.address_icon),
            Pair(getString(R.string.select_language), R.drawable.language_home),
            Pair(getString(R.string.terms_of_services), R.drawable.term_icon),
            Pair(getString(R.string.privacy_policy), R.drawable.privacy_policy_icon),
            Pair(getString(R.string.faq_support), R.drawable.help_suport_icon),
            Pair(getString(R.string.contact_us), R.drawable.contact_us_icon),
        )
        if (isLogin == "1") {
            profileItems.add(Pair(getString(R.string.delete_account), R.drawable.delete_account))
            profileItems.add(Pair(getString(R.string.logout), R.drawable.logout_icon))
        }
        // Set up adapter
        val adapter = ProfileAdapter(profileItems) { itemTitle ->
            // Handle item click
            when (itemTitle) {
                getString(R.string.wishlist) -> {

                    findNavController().navigate(R.id.action_profile_to_wishlist)
                }

                getString(R.string.my_orders) -> {

                    findNavController().navigate(R.id.action_profileFragment_to_myOrderFragment)
                }

                getString(R.string.manage_address) -> {

                    findNavController().navigate(R.id.action_profileFragment_to_mangeAddressFragment)
                }

                getString(R.string.select_language) -> {

                    findNavController().navigate(R.id.action_profileFragment_to_chooseLanguageFragment)
                }

                getString(R.string.terms_of_services) -> {
                    val bundle = Bundle()
                    bundle.putString("terms", "1")
                    findNavController().navigate(
                        R.id.action_profileFragment_to_termConditionFragment2,
                        bundle
                    )

                }

                getString(R.string.privacy_policy) -> {
                    // Handle Policy
                    findNavController().navigate(R.id.action_profileFragment_to_termConditionFragment2)

                }

                getString(R.string.faq_support) -> {

                    findNavController().navigate(R.id.action_profileFragment_to_helpSupportFragment)

                }

                getString(R.string.contact_us) -> {
                    findNavController().navigate(R.id.action_profileFragment_to_contactUsFragment)
                }

                getString(R.string.my_booking) -> {
                    findNavController().navigate(R.id.action_profileFragment_to_bookingFragment)
                }

                getString(R.string.delete_account) -> {
                    Log.d("TAG", "setupRecyclerView: delete account")
                    showSortBottomSheet("1")
                }

                getString(R.string.logout) -> {
                    Log.d("TAG", "setupRecyclerView: logout account")
                    showSortBottomSheet("2")
                }

            }
        }

        // Set up RecyclerView
        binding.itemsBagRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    /*private fun showSortBottomSheet(type:String) {
        val bottomSheet = LogoutBottomSheetFragment()
        bottomSheet.show(parentFragmentManager, "LogoutBottomSheetFragment")
    }*/
    private fun showSortBottomSheet(type: String) {
        val actionType = if (type == "1") "delete" else "logout"
        val bottomSheet = LogoutBottomSheetFragment.newInstance(actionType)
        bottomSheet.show(parentFragmentManager, "LogoutBottomSheetFragment")
    }


    private fun showLoginPrompt() {
        var dialog: AlertDialog? = null
        dialog = CommonUtils.showCustomAlertDialog(
            requireActivity(),
            getString(R.string.sign_up_required),
            getString(R.string.sign_up_required_to_go_to_profile),
            positiveButtonText = getString(R.string.sign_up),
            negativeButtonText = getString(R.string.cancel),
            positiveButtonAction = {
                dialog?.dismiss()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                val bundle = Bundle()
                requireActivity().startActivity(intent)
            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT)
            .show()
    }

}