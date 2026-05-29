package com.app.adhyatmah.presentation.ui.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemPujaGridBinding
import com.app.adhyatmah.domain.model.get_services.Puja
import com.app.adhyatmah.utils.formatViews
import com.app.adhyatmah.utils.getDigit
import com.bumptech.glide.Glide

class PujaAdapter(
    private val onSelected: (Puja) -> Unit,
) : BaseRecyclerAdapter<ListItemPujaGridBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_puja_grid

    private val pujaList: ArrayList<Puja> = ArrayList()

    override fun getItemCount(): Int {
        return pujaList.size
    }

    override fun bind(binding: ListItemPujaGridBinding, position: Int) {
        binding.apply {
            val context = binding.root.context
            val puja = pujaList[position]

            puja.image?.let { url ->
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivPic)
            }

            tvViews.text = formatViews(puja.views ?: 0)
            tvPoojaName.text = puja.poojaType ?: ""
            tvPoojaTime.text = puja.duration ?: ""
            tvOfferPrice.text = (puja.price?.getDigit() ?: "")
            tvOldPrice.text = (puja.price?.getDigit() ?: "")

            clBookNow.setOnClickListener {
                onSelected(puja)
            }
        }
    }

    fun addList(list: ArrayList<Puja>) {
        pujaList.clear()
        pujaList.addAll(list)
        notifyDataSetChanged()
    }
}