package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.MyOrderDetailsItemContainerBinding

class MyOrderDetailsAdapter(
    private var orders: MutableList<com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details.Item>,
) : RecyclerView.Adapter<MyOrderDetailsAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: MyOrderDetailsItemContainerBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyOrderDetailsItemContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val order = orders[position]
        with(holder.binding) {
            ordrIds.text = order.name ?: context.getString(R.string.n_a)

            title.text = order.type ?: context.getString(R.string.n_a)
            val quantityText = "${context.getString(R.string.quantity)} ${order.quantity}"
            descre.text = quantityText

            Glide.with(holder.itemView.context)
                .load(order.image)
                .into(srcImg)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun updateOrder(newOrders: List<com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details.Item>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }
}