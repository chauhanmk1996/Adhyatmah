package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.ViewPagerBinding
import com.app.adhyatmah.domain.model.product_detail_response.Image


class ViewPagerProductDetailAdapter (
    var viewPagerList: List<Image>,
) : RecyclerView.Adapter<ViewPagerProductDetailAdapter.ViewModel>() {
    inner class ViewModel(var binding: ViewPagerBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerProductDetailAdapter.ViewModel {
        var binding = ViewPagerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewModel(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerProductDetailAdapter.ViewModel, position: Int) {
        var list = viewPagerList[position]
//        var ll = vViewPage[position]
        // holder.binding.name.text = list.name
        Glide.with(holder.itemView.context)
            .load(list.url)
            .into(holder.binding.onboardingImage)


    }

    override fun getItemCount(): Int {
        return viewPagerList.size /*agentList.size
    */}

}