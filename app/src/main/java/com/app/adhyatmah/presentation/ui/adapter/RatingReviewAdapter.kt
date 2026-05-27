package com.app.adhyatmah.presentation.ui.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BasePagerAdapter
import com.app.adhyatmah.databinding.ViewPagerRatingReviewBinding
import com.app.adhyatmah.domain.model.Testimonials
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show

class RatingReviewAdapter(private var rateReviewList: ArrayList<Testimonials>) :
    BasePagerAdapter<ViewPagerRatingReviewBinding>() {

    override fun getLayoutId(): Int = R.layout.view_pager_rating_review

    override fun getCount(): Int {
        return rateReviewList.size
    }

    override fun bind(binding: ViewPagerRatingReviewBinding, position: Int) {
        val rateReview = rateReviewList[position]
        binding.apply {
            tvName.text = rateReview.name ?: ""
            tvDaysAgo.text = rateReview.daysAgo ?: ""

            when (rateReview.stars) {
                "1" -> {
                    ivStar1.show()
                    ivStar2.hide()
                    ivStar3.hide()
                    ivStar4.hide()
                    ivStar5.hide()
                }

                "2" -> {
                    ivStar1.show()
                    ivStar2.show()
                    ivStar3.hide()
                    ivStar4.hide()
                    ivStar5.hide()
                }

                "3" -> {
                    ivStar1.show()
                    ivStar2.show()
                    ivStar3.show()
                    ivStar4.hide()
                    ivStar5.hide()
                }

                "4" -> {
                    ivStar1.show()
                    ivStar2.show()
                    ivStar3.show()
                    ivStar4.show()
                    ivStar5.hide()
                }

                "5" -> {
                    ivStar1.show()
                    ivStar2.show()
                    ivStar3.show()
                    ivStar4.show()
                    ivStar5.show()
                }
            }

            tvService.text = rateReview.service ?: ""
            tvReview.text = rateReview.review ?: ""
        }
    }
}