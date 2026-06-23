package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.app.adhyatmah.databinding.TrackorderListLayoutBinding
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.adhyatmah.domain.model.TrackItem

class TrackOrderAdapter(val trackingList: MutableList<TrackItem>) :
    Adapter<TrackOrderAdapter.ViewModel>() {

    class ViewModel(var binding: TrackorderListLayoutBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val binding =
            TrackorderListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val list = trackingList[position]
        holder.binding.packageTv.text = list.name
        holder.binding.trackingTimeTv.text = list.trackingdate
    }

    override fun getItemCount(): Int {
        return trackingList.size
    }
}