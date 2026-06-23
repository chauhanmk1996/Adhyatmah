package com.app.adhyatmah.presentation.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ProductDetailReviewRecyclerBinding
import com.app.adhyatmah.domain.model.ProductReviewListResponse.Review
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ProductDetailReviewAdapter(
    private val items: MutableList<Review>,
    private val viewMoreClickListener: OnViewMoreClickListener,
) : RecyclerView.Adapter<ProductDetailReviewAdapter.ViewPagger>() {

    private var showAll = false

    class ViewPagger(
        var binding: ProductDetailReviewRecyclerBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    fun updateItems(newItems: List<Review>) {
        showAll = false
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewPagger {
        val binding =
            ProductDetailReviewRecyclerBinding.inflate(LayoutInflater.from(parent.context))
        return ViewPagger(binding)
    }

    override fun onBindViewHolder(holder: ViewPagger, position: Int) {
        val context = holder.itemView.context
        val item = items[position]
        holder.binding.userName.text = item.reviewer.name
        holder.binding.userReview.text = item.body
        holder.binding.rating.rating = item.rating.toFloat()
        holder.binding.dayId.text = getTimeAgo(context,item.created_at)
        holder.binding.rating.progressTintList = ColorStateList.valueOf(Color.BLACK)
        holder.binding.rating.secondaryProgressTintList = ColorStateList.valueOf(Color.BLACK)
        holder.binding.rating.progressBackgroundTintList = ColorStateList.valueOf(Color.GRAY)
        val isLastVisible = !showAll && position == 2 && items.size > 3

        holder.binding.viewMore.let { viewMore ->
            viewMore.visibility = if (isLastVisible) View.VISIBLE else View.GONE
            if (isLastVisible) {
                viewMore.setOnClickListener {
                    viewMoreClickListener.onViewMoreClicked()
                    notifyDataSetChanged()
                }
            } else {
                viewMore.setOnClickListener(null)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (showAll) items.size else minOf(3, items.size)
    }

    interface OnViewMoreClickListener {
        fun onViewMoreClicked()
    }

    fun getTimeAgo(context: Context, createdAt: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val time = LocalDateTime.parse(createdAt, formatter)
        val now = LocalDateTime.now(ZoneId.of("UTC"))

        val duration = Duration.between(time, now)

        return when {
            duration.toMinutes() < 1 -> context.getString(R.string.just_now)
            duration.toMinutes() < 60 -> "${duration.toMinutes()} ${context.getString(R.string.minutes_ago)}"
            duration.toHours() < 24 -> "${duration.toHours()} ${context.getString(R.string.hours_ago)}"
            duration.toDays() < 7 -> "${duration.toDays()} ${context.getString(R.string.days_ago)}"
            else -> time.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        }
    }
}