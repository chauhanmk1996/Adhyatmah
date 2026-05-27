package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.REVIEW_PD_ID
import com.app.adhyatmah.databinding.FragmentReviewsBinding
import com.app.adhyatmah.domain.model.reviews.Review
import com.app.adhyatmah.presentation.ui.adapter.ReviewsAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue
import kotlin.toString

class ReviewsFragment : BaseFragment<FragmentReviewsBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private lateinit var reviewsAdapter: ReviewsAdapter

    private var isLoading = false
    private var currentPage = 1
    private val limitPerPage = 10
    private var productId =""

    override fun setLayout(): Int {
        return R.layout.fragment_reviews
    }

    override fun initView(savedInstanceState: Bundle?) {
        // Initialize adapter with an empty list
        productId = Preferences.getStringPreference(requireContext(), REVIEW_PD_ID).toString()

        reviewsAdapter = ReviewsAdapter(mutableListOf())
        binding.reviewsRecyclerId.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = reviewsAdapter
        }

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

//        fetchReviews()
        setObserver()
        homeViewModel.getAllProductReview(productId,currentPage,limitPerPage)

    }

    private fun setObserver() {

        homeViewModel.getAllPdReviewLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.product.reviews

                            Log.d("Tag","Ihjhfds: ${data.size}")

                            val reviewContainer = it.data.payload.product.reviews
                            reviewsAdapter.updateReviews(reviewContainer)

                            Log.d("tt","sdsfdsfds, $id")
                            Log.d("Tdkjd",data.toString())
//                          setAdapter(data)

                        }
                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }

            }

        }

    }


    private fun fetchReviews() {
        // Simulated API call (replace with actual API call)
        val newReviews = mutableListOf(
            Review("Emily", "Perfect. Must buy!", "3 days ago", R.drawable.ratings_icon, R.drawable.model_pic),
            Review("Emily", "Perfect. Must buy!", "3 days ago", R.drawable.ratings_icon, R.drawable.model_pic),
            Review("Emily", "Perfect. Must buy!", "3 days ago", R.drawable.ratings_icon, R.drawable.model_pic),
            Review("Emily", "Perfect. Must buy!", "3 days ago", R.drawable.ratings_icon, R.drawable.model_pic),
            Review("Emily", "Perfect. Must buy!", "3 days ago", R.drawable.ratings_icon, R.drawable.model_pic),
            )

        // Update the adapter with new reviews
//        reviewsAdapter.updateReviews(newReviews)
    }
}