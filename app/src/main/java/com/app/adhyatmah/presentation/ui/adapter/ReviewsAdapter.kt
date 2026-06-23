package com.app.adhyatmah.presentation.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ReviewsLayoutListBinding
import com.app.adhyatmah.domain.model.ProductReviewListResponse
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReviewsAdapter(
    var reviews: MutableList<ProductReviewListResponse.Review>,
) : RecyclerView.Adapter<ReviewsAdapter.ViewModel>() {

    class ViewModel(
        var binding: ReviewsLayoutListBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val binding = ReviewsLayoutListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewModel(binding)
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val context = holder.itemView.context
        val review = reviews[position]
        holder.binding.userName.text = review.reviewer.name
        holder.binding.userReview.text = review.body
        holder.binding.dayId.text = getTimeAgo(context, review.created_at)
        holder.binding.rating.rating = review.rating.toFloat()
        holder.binding.rating.progressTintList = ColorStateList.valueOf(Color.BLACK)
        holder.binding.rating.secondaryProgressTintList = ColorStateList.valueOf(Color.BLACK)
        holder.binding.rating.progressBackgroundTintList = ColorStateList.valueOf(Color.GRAY)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun updateReviews(newReviews: List<ProductReviewListResponse.Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
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