package com.app.adhyatmah.presentation.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.MyOrderDetailsItemContainerBinding
import com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders.Order
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MyOrderDetailsAdapter(
    private var orders: MutableList<com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details.Item>,

    ) : RecyclerView.Adapter<MyOrderDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: MyOrderDetailsItemContainerBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyOrderDetailsItemContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

      /*  holder.binding.root.setOnClickListener {
            onMyOrderDtChangeListener.onMyOrderDeChanged(order)
            Log.d("Tag","Inifdfd: ${order.id}")
        }*/
        with(holder.binding) {
//            ordrIds.text = order.title ?: "N/A"
            ordrIds.text = order.name ?: "N/A"

            //ordrIds.text = order.title?.toString() ?: ""
//            title.text = order.variant_title ?: "N/A"
            title.text = order.type ?: "N/A"
//               descre.text = "Quantity "+order.current_quantity.toString()?:"N/A"
               descre.text = "Quantity "+order.quantity.toString()?:"N/A"

//            itemPriceTv.text = order.total_line_items_price?.toString()?: ""
//            orderDateTv.text = getTimeAgo(order.created_at.toString())

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeAgo(createdAt: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val time = LocalDateTime.parse(createdAt, formatter)
        val now = LocalDateTime.now(ZoneId.of("UTC"))

        val duration = Duration.between(time, now)

        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            duration.toDays() < 7 -> "${duration.toDays()} days ago"
            else -> time.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        }
    }

    interface OnMyOrderDetailsChangeListener {
        fun onMyOrderDeChanged(order: Order)
        fun onTrackClicked(orderId: String)

    }

}