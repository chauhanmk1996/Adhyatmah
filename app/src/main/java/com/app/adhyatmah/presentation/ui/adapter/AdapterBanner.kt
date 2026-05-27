package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.HomeBannerRecyclerviewBinding
import com.app.adhyatmah.domain.model.home_banner_response.SubBanner

class AdapterBanner(var data: List<SubBanner>) : RecyclerView.Adapter<AdapterBanner.ViewPagger>() {
    class ViewPagger(var binding: HomeBannerRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagger {
        var binding = HomeBannerRecyclerviewBinding.inflate(LayoutInflater.from(parent.context))
        return ViewPagger(binding)
    }

    override fun onBindViewHolder(holder: ViewPagger, position: Int) {
        var list = data[position]


        Glide.with(holder.itemView.context).load(list.url).into(holder.binding.img)

    }

    override fun getItemCount(): Int {
        return data.size
    }
}