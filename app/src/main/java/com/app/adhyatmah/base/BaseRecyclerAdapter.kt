package com.app.adhyatmah.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<VB : ViewDataBinding> :
    RecyclerView.Adapter<BaseRecyclerAdapter.MyViewHolder<VB>>() {

    class MyViewHolder<VB : ViewDataBinding>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root)

    abstract fun getLayoutId(): Int
    abstract fun bind(binding: VB, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<VB> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<VB>(inflater, getLayoutId(), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder<VB>, position: Int) {
        bind(holder.binding, position)
        holder.binding.executePendingBindings()
    }
}