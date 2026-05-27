package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentSizeImageBinding
import com.app.adhyatmah.utils.base.BaseFragment

class SizeImageFragment : BaseFragment<FragmentSizeImageBinding>() {
    override fun setLayout(): Int {
        return R.layout.fragment_size_image
    }

    override fun initView(savedInstanceState: Bundle?) {

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }


}