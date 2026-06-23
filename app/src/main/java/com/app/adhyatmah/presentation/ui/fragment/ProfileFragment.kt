package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.app.adhyatmah.presentation.ui.bottom_sheet.SignUpRequiredBottomSheetFragment
import com.app.adhyatmah.utils.base.BaseFragment

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
        setupRecyclerView()
    }

    fun setProfileData() {
        isLogin = Preferences.getStringPreference(requireContext(), IS_LOGIN).toString()
        val img = Preferences.getStringPreference(requireContext(), PROFILE_IMG).toString()
        val profileData =
            Preferences.getCustomModelPreference<Payload>(requireContext(), IS_PROFILE_DATA)
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

        if (img.isEmpty()) {
            binding.img.setImageResource(R.drawable.placeholder_icon)
        } else {
            Glide.with(requireContext())
                .load(img)
                .error(R.drawable.placeholder_icon) // if loading fails
                .into(binding.img)
        }

        binding.rightArrowImg.setOnClickListener {
            if (isLogin == "1") {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            } else {
                signupRequired(getString(R.string.sign_up_required_to_go_to_profile))
            }
        }
    }

    private fun setupRecyclerView() {
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

        val adapter = ProfileAdapter(profileItems) { itemTitle ->
            when (itemTitle) {
                getString(R.string.wishlist) -> {
                    if (isLogin == "1") {
                        findNavController().navigate(R.id.action_profile_to_wishlist)
                    }else {
                        signupRequired(getString(R.string.please_sign_up_required_to_see_wishlist))
                    }
                }

                getString(R.string.my_orders) -> {
                    if (isLogin == "1") {
                        findNavController().navigate(R.id.action_profileFragment_to_myOrderFragment)
                    }else {
                        signupRequired(getString(R.string.please_sign_up_required_to_see_my_order))
                    }
                }

                getString(R.string.my_booking) -> {
                    if (isLogin == "1") {
                        findNavController().navigate(R.id.action_profileFragment_to_bookingFragment)
                    }else {
                        signupRequired(getString(R.string.please_sign_up_required_to_see_my_booking))
                    }
                }

                getString(R.string.manage_address) -> {
                    if (isLogin == "1") {
                        findNavController().navigate(R.id.action_profileFragment_to_mangeAddressFragment)
                    }else {
                        signupRequired(getString(R.string.please_sign_up_required_to_see_manage_address))
                    }
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
                    findNavController().navigate(R.id.action_profileFragment_to_termConditionFragment2)
                }

                getString(R.string.faq_support) -> {
                    findNavController().navigate(R.id.action_profileFragment_to_helpSupportFragment)
                }

                getString(R.string.contact_us) -> {
                    findNavController().navigate(R.id.action_profileFragment_to_contactUsFragment)
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

        binding.itemsBagRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    private fun showSortBottomSheet(type: String) {
        val actionType = if (type == "1") "delete" else "logout"
        val bottomSheet = LogoutBottomSheetFragment.newInstance(actionType)
        bottomSheet.show(parentFragmentManager, "LogoutBottomSheetFragment")
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
}