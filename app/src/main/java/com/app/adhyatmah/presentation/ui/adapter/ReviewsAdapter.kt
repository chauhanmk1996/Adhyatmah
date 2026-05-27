package com.app.adhyatmah.presentation.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.ReviewsLayoutListBinding
import com.app.adhyatmah.domain.model.ProductReviewListResponse
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReviewsAdapter(
    var reviews: MutableList<ProductReviewListResponse.Review> // MutableList for dynamic updates
) : RecyclerView.Adapter<ReviewsAdapter.ViewModel>() {

    inner class ViewModel(
        var binding: ReviewsLayoutListBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val binding = ReviewsLayoutListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewModel(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val review = reviews[position]
        holder.binding.userName.text = review.reviewer.name
        holder.binding.userReview.text = review.body
        holder.binding.dayId.text = getTimeAgo(review.created_at.toString())
        holder.binding.rating.rating = review.rating?.toFloat() ?: 0f

        holder.binding.rating.progressTintList = ColorStateList.valueOf(Color.BLACK)
        holder.binding.rating.secondaryProgressTintList = ColorStateList.valueOf(Color.BLACK)
        holder.binding.rating.progressBackgroundTintList = ColorStateList.valueOf(Color.GRAY) // optional


        /* Glide.with(holder.binding.ratingsBtn.context)
             .load(review.pictures)
             .into(holder.binding.ratingsBtn)
        */

       /* // Load profile picture inside CardView using Glide
        Glide.with(holder.binding.profileCard.context)
            .load(review.profilePicResId)
            .into(
                holder.binding.profileCard.findViewById<AppCompatImageView>(
                    com.example.ronthompson.R.id.model_pic
                )
            )*/
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    // Registers a new chunk in the stream
    fun updateReviews(newReviews: List<ProductReviewListResponse.Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
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

}