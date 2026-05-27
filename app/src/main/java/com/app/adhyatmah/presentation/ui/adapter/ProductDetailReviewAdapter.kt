package com.app.adhyatmah.presentation.ui.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.ProductDetailReviewRecyclerBinding
import com.app.adhyatmah.domain.model.ProductReviewListResponse
import com.app.adhyatmah.domain.model.ProductReviewListResponse.Review
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ProductDetailReviewAdapter(
    private val items: MutableList<ProductReviewListResponse.Review>,
    private val viewMoreClickListener: OnViewMoreClickListener

):RecyclerView.Adapter<ProductDetailReviewAdapter.ViewPagger>() {

    private var showAll = false

    inner  class ViewPagger (
        var binding:ProductDetailReviewRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root){

    }


    @SuppressLint("SuspiciousIndentation")
    fun updateItems(newItems: List<Review>) {
        showAll = false
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged() // Always safe
    }


    /*fun updateItems(newItems: List<ProductReviewListResponse.Review>) {
        showAll = false
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }
*/


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailReviewAdapter.ViewPagger {
        var binding = ProductDetailReviewRecyclerBinding.inflate(LayoutInflater.from(parent.context))
        return ViewPagger(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ProductDetailReviewAdapter.ViewPagger, position: Int) {
        val item = items[position]

        holder.binding.userName.text = item.reviewer.name
        holder.binding.userReview.text = item.body
        holder.binding.rating.rating = item.rating?.toFloat() ?: 0f
        holder.binding.dayId.text = getTimeAgo(item.created_at.toString())

        holder.binding.rating.progressTintList = ColorStateList.valueOf(Color.BLACK)
        holder.binding.rating.secondaryProgressTintList = ColorStateList.valueOf(Color.BLACK)
        holder.binding.rating.progressBackgroundTintList = ColorStateList.valueOf(Color.GRAY) // optional


        /*Glide.with(holder.itemView.context)
            .load(item.pictures)
            .into(holder.binding.userImage)
*/

        val isLastVisible = !showAll && position == 2 && items.size > 3

        holder.binding.viewMore?.let { viewMore ->
            viewMore.visibility = if (isLastVisible) View.VISIBLE else View.GONE

            if (isLastVisible) {
                viewMore.setOnClickListener {
//                    showAll = true
                    viewMoreClickListener.onViewMoreClicked()
                    notifyDataSetChanged()
                }
            } else {
                viewMore.setOnClickListener(null)
            }
        }


       /* val isLastVisible = !showAll && position == 2 && items.size > 3
        holder.binding.viewMore.visibility = if (isLastVisible) View.VISIBLE else View.GONE

        holder.binding.viewMore.setOnClickListener {
            showAll = true
            notifyDataSetChanged()
        }
*/


/*
        val isLastItem = position == itemCount - 1
        holder.binding.viewMore.visibility = if (isLastItem) View.VISIBLE else View.GONE
*/

    }


    override fun getItemCount(): Int {
//        return items.size
        return if (showAll) items.size else minOf(3, items.size)

    }
    interface OnViewMoreClickListener {
        fun onViewMoreClicked()
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



}