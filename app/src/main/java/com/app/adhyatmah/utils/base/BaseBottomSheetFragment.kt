package com.app.adhyatmah.utils.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<VB : ViewBinding> : BottomSheetDialogFragment() {

    lateinit var binding: VB
    lateinit var mContext: Context

    abstract fun setLayout(): Int
    abstract fun initView(savedInstanceState: Bundle?)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (!this::binding.isInitialized) {
            binding = DataBindingUtil.inflate(inflater, setLayout(), container, false)
            initView(savedInstanceState)
        }
        return binding.root
    }
}
