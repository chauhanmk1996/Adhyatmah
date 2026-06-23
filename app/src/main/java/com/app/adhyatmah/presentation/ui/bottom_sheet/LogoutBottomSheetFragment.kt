package com.app.adhyatmah.presentation.ui.bottom_sheet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.FCM_TOKEN
import com.app.adhyatmah.data.preferences.SELECTED_LANGUAGE_CODE
import com.app.adhyatmah.data.preferences.SELECTED_LANGUAGE_NAME
import com.app.adhyatmah.databinding.FragmentLogoutBottomSheetBinding
import com.app.adhyatmah.domain.model.delete_account.delete_request.DeleteRequest
import com.app.adhyatmah.domain.model.logout.LogOutRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.utils.base.BaseBottomSheetFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.gson.Gson

class LogoutBottomSheetFragment : BaseBottomSheetFragment<FragmentLogoutBottomSheetBinding>() {

    val viewModel by activityViewModels<FilterViewModel>()

    override fun setLayout(): Int {
        return R.layout.fragment_logout_bottom_sheet
    }

    override fun initView(savedInstanceState: Bundle?) {
        val token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN)
        val logOutRequestData = LogOutRequest()
        logOutRequestData.accessToken = token?.trim() ?: ""
        val deleteToken = DeleteRequest()
        deleteToken.customerToken = token?.trim() ?: ""

        if (actionType == "delete") {
            binding.logoutTitle.text = getString(R.string.delete_account)
            binding.logoutMessage.text =
                getString(R.string.are_you_sure_you_want_to_delete_your_account)
            binding.btnConfirm.text = getString(R.string.account)
            binding.btnConfirm.setOnClickListener {
                viewModel.hitDeleteApi(deleteToken)
            }
        } else {
            binding.logoutMessage.text = getString(R.string.are_you_sure_you_want_to_logout)
            binding.btnConfirm.text = getString(R.string.logout)
            binding.logoutTitle.text = getString(R.string.logout)
            binding.btnConfirm.setOnClickListener {
                viewModel.hitLogOutApi(logOutRequestData)
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        setObserve()
    }

    override fun getTheme(): Int = R.style.TransparentBottomSheetDialog


    fun setObserve() {
        viewModel.getLogout().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == 1) {
                        if (it.data.code == 200) {
                            dismiss()
                            Preferences.removeAllPreferencesExcept(
                                requireContext(), listOf(
                                    FCM_TOKEN,
                                    SELECTED_LANGUAGE_CODE, SELECTED_LANGUAGE_NAME
                                )
                            )
                            val intent = Intent(requireActivity(), LoginActivity::class.java)
                            intent.putExtra("screen", "login")
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        viewModel.getDeleteAccount().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == 1) {
                        if (it.data.code == 200) {
                            dismiss()
                            Preferences.removeAllPreferencesExcept(
                                requireContext(),
                                listOf(FCM_TOKEN, SELECTED_LANGUAGE_NAME, SELECTED_LANGUAGE_CODE)
                            )
                            val intent = Intent(requireActivity(), LoginActivity::class.java)
                            intent.putExtra("screen", "login")
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    private var actionType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionType = arguments?.getString("type")
    }

    companion object {
        fun newInstance(actionType: String): LogoutBottomSheetFragment {
            val fragment = LogoutBottomSheetFragment()
            val args = Bundle()
            args.putString("type", actionType)
            fragment.arguments = args
            return fragment
        }
    }
}