package com.app.adhyatmah.presentation.ui.bottom_sheet

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentSignUpRequiredBottomSheetBinding
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.utils.base.BaseBottomSheetFragment

class SignUpRequiredBottomSheetFragment(val message: String, val signUpClick: () -> Unit) :
    BaseBottomSheetFragment<FragmentSignUpRequiredBottomSheetBinding>() {

    val viewModel by activityViewModels<FilterViewModel>()

    override fun setLayout(): Int {
        return R.layout.fragment_sign_up_required_bottom_sheet
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.tvMessage.text = message

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSignUp.setOnClickListener {
            signUpClick()
            dismiss()
        }
    }

    override fun getTheme(): Int = R.style.TransparentBottomSheetDialog
}