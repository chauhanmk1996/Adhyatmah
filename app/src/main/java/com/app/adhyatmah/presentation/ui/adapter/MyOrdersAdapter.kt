package com.app.adhyatmah.presentation.ui.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.MyordersLayoutListBinding
import com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders.Order
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MyOrdersAdapter(
    private var orders: MutableList<Order>,
    private val onMyOrderDtChangeListener: OnMyOrderDetailsChangeListener

) : RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: MyordersLayoutListBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyordersLayoutListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        holder.binding.root.setOnClickListener {
            onMyOrderDtChangeListener.onMyOrderDeChanged(order)
//            Log.d("Tag","Inifdfd: ${order.id}")
        }
        with(holder.binding) {
//            greyTopTv.text = order.name?.toString() ?: "Unknown Order Name"
            greyTopTv.text = order.title ?: "Unknown Order Name"
//            itemPriceTv.text = order.presentment_currency+" "+ order.total_line_items_price?.toString()?: ""
            itemPriceTv.text = "₹ "+order.amount.toString()
//            orderDateTv.text = getTimeAgo(order.created_at ?: "")
//            orderDateTv.text = getTimeAgo(order.time ?: "")
            orderDateTv.text = order.date +", "+ order.time
          if(order.track?.status=="canceled"){
              trackText.text=holder.binding.root.context.getString(R.string.cancelled)
              trackLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.rectangle_cancel)

          }else{
              trackLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.rectangle_track)
              trackText.text=holder.binding.root.context.getString(R.string.track)
              trackLayout.setOnClickListener {
//                  onMyOrderDtChangeListener.onTrackClicked(order.id.toString())
                  onMyOrderDtChangeListener.onTrackClicked(order.orderId.toString())
                  // Toast.makeText(root.context, "Tracking order: Under Development",Toast.LENGTH_SHORT).show()
              }

          }

                /*Glide.with(holder.itemView.context)
                    .load(order.line_items[0].product_image.src).error(R.drawable.placeholder_icon)
                    .into(imgIv)

    */
//            val productImageSrc = order.line_items?.getOrNull(0)?.product_image?.src
            val productImageSrc = order.image

            if (productImageSrc != null) {
                Glide.with(holder.itemView.context)
                    .load(productImageSrc)
                    .error(R.drawable.placeholder_icon)
                    .into(imgIv)
            } else {
                Glide.with(holder.itemView.context)
                    .load(R.drawable.placeholder_icon)
                    .into(imgIv) // Load a placeholder image if the product image is null
            }

//           trackText.text = order["trackStatus"]?.toString() ?: "Track"
//           orderDateTv.text = order["date"]?.toString() ?: "" //

///*          itemPriceTv.text = order["price"]?.toString() ?: ""
//            trackText.text = order["trackStatus"]?.toString() ?: "Track"
//            orderDateTv.text = order["date"]?.toString() ?: "" // Use the new ID

//          imgIv.setImageResource(order["imageResId"]?.toString()?.toIntOrNull() ?: R.drawable.model_image2)




        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        /*if (newOrders != null) {
            orderList.addAll(newOrders)
        }*/
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeAgo(createdAt: String): String {
        if (createdAt.isEmpty()) {
            return "Unknown date" // Return an empty string or a default value like "Unknown date"
        }
        try {
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
        } catch (e: Exception) {
            return "" // Or return the original string, or log the error
        }
    }

    interface OnMyOrderDetailsChangeListener {
        fun onMyOrderDeChanged(order: Order)
        fun onTrackClicked(orderId: String)

    }

}