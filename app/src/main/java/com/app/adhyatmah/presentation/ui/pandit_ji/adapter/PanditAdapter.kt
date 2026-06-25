package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemPanditBinding
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.Vendor
import com.bumptech.glide.Glide

class PanditJiAdapter(
    private val onSelected: (Vendor) -> Unit,
) : BaseRecyclerAdapter<ListItemPanditBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_pandit

    private val panditJiList: ArrayList<Vendor> = ArrayList()

    override fun getItemCount(): Int {
        return panditJiList.size
    }

    override fun bind(binding: ListItemPanditBinding, position: Int) {
        binding.apply {
            val context = binding.root.context
            val panditJi = panditJiList[position]

            panditJi.image?.url?.let { url ->
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivProfilePic)
            }

            val name = "${panditJi.firstName ?: ""} ${panditJi.lastName ?: ""}"
            tvName.text = name

            val experience =
                "${panditJi.city ?: ""} | ${panditJi.seoContent?.details?.experience ?: ""}"
            tvExperience.text = experience

            val about = "${panditJi.about ?: ""}  ★ 4.8"
            tvAbout.text = about

            clPanditJi.setOnClickListener {
                onSelected(panditJi)
            }
        }
    }

    fun addList(list: ArrayList<Vendor>) {
        val startPosition = panditJiList.size
        panditJiList.addAll(list)
        notifyItemRangeInserted(startPosition, list.size)
    }

    fun clearList() {
        panditJiList.clear()
        notifyDataSetChanged()
    }
}