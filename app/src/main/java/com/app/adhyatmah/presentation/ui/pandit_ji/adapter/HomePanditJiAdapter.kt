package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemPanditJiBinding
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.Vendor
import com.app.adhyatmah.utils.formatViews
import com.bumptech.glide.Glide

class HomePanditJiAdapter(
    private val panditJiList: ArrayList<Vendor>, private val onSelected: (Int) -> Unit,
) : BaseRecyclerAdapter<ListItemPanditJiBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_pandit_ji

    override fun getItemCount(): Int {
        return panditJiList.size
    }

    override fun bind(binding: ListItemPanditJiBinding, position: Int) {
        binding.apply {
            val context = binding.root.context
            val panditJi = panditJiList[position]

            tvViews.text = formatViews(panditJi.views ?: 0)

            panditJi.image?.url?.let { url ->
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivPic)
            }

            tvAbout.text = panditJi.about ?: ""

            val name = "${panditJi.firstName ?: ""} ${panditJi.lastName ?: ""}"
            tvName.text = name

            val experience = "${panditJi.experience ?: ""} Years Exp."
            tvExperience.text = experience

            cvPanditJi.setOnClickListener {
                onSelected(position)
            }
        }
    }
}