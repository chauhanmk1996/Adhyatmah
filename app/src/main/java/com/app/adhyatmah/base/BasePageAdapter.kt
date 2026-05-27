package com.app.adhyatmah.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.PagerAdapter

abstract class BasePagerAdapter<VB : ViewDataBinding> : PagerAdapter() {

    abstract fun getLayoutId(): Int
    abstract fun bind(binding: VB, position: Int)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val binding = DataBindingUtil.inflate<VB>(
            inflater, getLayoutId(), container, false
        )

        bind(binding, position)

        container.addView(binding.root)
        binding.executePendingBindings()
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj
}
