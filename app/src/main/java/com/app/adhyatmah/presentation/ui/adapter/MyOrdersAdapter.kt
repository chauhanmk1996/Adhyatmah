package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.MyordersLayoutListBinding
import com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders.Order

class MyOrdersAdapter(
    private var orders: MutableList<Order>,
    private val onMyOrderDtChangeListener: OnMyOrderDetailsChangeListener,
) : RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: MyordersLayoutListBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyordersLayoutListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val order = orders[position]

        holder.binding.root.setOnClickListener {
            onMyOrderDtChangeListener.onMyOrderDeChanged(order)
        }

        with(holder.binding) {
            greyTopTv.text = order.title ?: context.getString(R.string.unknown_order_name)
            val priceText = "₹ " + order.amount.toString()
            itemPriceTv.text = priceText
            val orderDateText = order.date + ", " + order.time
            orderDateTv.text = orderDateText

            if (order.track?.status == "canceled") {
                trackText.text = holder.binding.root.context.getString(R.string.cancelled)
                trackLayout.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.rectangle_cancel)
            } else {
                trackLayout.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.rectangle_track)
                trackText.text = holder.binding.root.context.getString(R.string.track)
                trackLayout.setOnClickListener {
                    onMyOrderDtChangeListener.onTrackClicked(order.orderId.toString())
                }
            }

            val productImageSrc = order.image
            if (productImageSrc != null) {
                Glide.with(holder.itemView.context)
                    .load(productImageSrc)
                    .error(R.drawable.placeholder_icon)
                    .into(imgIv)
            } else {
                Glide.with(holder.itemView.context)
                    .load(R.drawable.placeholder_icon)
                    .into(imgIv)
            }
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    interface OnMyOrderDetailsChangeListener {
        fun onMyOrderDeChanged(order: Order)
        fun onTrackClicked(orderId: String)
    }
}