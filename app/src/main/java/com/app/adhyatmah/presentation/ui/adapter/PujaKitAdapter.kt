package com.app.adhyatmah.presentation.ui.adapter

import android.widget.Toast
import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemPujaKitGridBinding
import com.app.adhyatmah.domain.model.get_services.PujaKit
import com.app.adhyatmah.utils.getDigit
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.bumptech.glide.Glide

class PujaKitAdapter(
    private val minusClick: (Int) -> Unit,
    private val addClick: (Int) -> Unit,
) : BaseRecyclerAdapter<ListItemPujaKitGridBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_puja_kit_grid

    private val pujaKitList: ArrayList<PujaKit> = ArrayList()

    override fun getItemCount(): Int {
        return pujaKitList.size
    }

    override fun bind(binding: ListItemPujaKitGridBinding, position: Int) {
        val context = binding.root.context
        binding.apply {
            val pujaKit = pujaKitList[position]

            pujaKit.image?.let { url ->
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivPic)
            }

            tvPoojaKitName.text = pujaKit.name ?: ""

            tvOfferPrice.text = (pujaKit.price?.getDigit() ?: "")
            tvOldPrice.text = (pujaKit.originalPrice?.getDigit() ?: "")

            val quantity = pujaKit.quantity ?: 0
            if (quantity == 0) {
                clAdd.show()
                clQuantity.hide()
            } else {
                clAdd.hide()
                clQuantity.show()
                tvQuantity.text = quantity.toString()
            }

            tvMinus.setOnClickListener {
                if (quantity > 0) {
                    minusClick(position)
                }
            }

            tvPlus.setOnClickListener {
                if (quantity < 5) {
                    addClick(position)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.quantity_can_t_be_more_then_5),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            clAdd.setOnClickListener {
                addClick(position)
            }
        }
    }

    fun addList(list: ArrayList<PujaKit>) {
        pujaKitList.clear()
        pujaKitList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateQuantity(pos: Int, quantity: Int) {
        pujaKitList[pos].quantity = quantity
        notifyItemChanged(pos)
    }

    fun removePos(pos: Int) {
        pujaKitList.removeAt(pos)
        notifyItemRemoved(pos)
    }
}