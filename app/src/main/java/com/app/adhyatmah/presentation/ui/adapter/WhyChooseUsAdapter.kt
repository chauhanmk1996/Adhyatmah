package com.app.adhyatmah.presentation.ui.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemWhyChooseUsBinding
import com.app.adhyatmah.domain.model.WhyChooseUs
import com.bumptech.glide.Glide

class WhyChooseUsAdapter(
    private val whyChooseUsList: ArrayList<WhyChooseUs>, private val onSelected: (Int) -> Unit,
) : BaseRecyclerAdapter<ListItemWhyChooseUsBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_why_choose_us

    override fun getItemCount(): Int {
        return whyChooseUsList.size
    }

    override fun bind(binding: ListItemWhyChooseUsBinding, position: Int) {
        binding.apply {
            val context = binding.root.context
            val whyChooseUs = whyChooseUsList[position]

            whyChooseUs.url?.let { url ->
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivPic)
            }

            tvTitle.text = whyChooseUs.title ?: ""

            clWhyChooseUs.setOnClickListener {
                onSelected(position)
            }
        }
    }
}