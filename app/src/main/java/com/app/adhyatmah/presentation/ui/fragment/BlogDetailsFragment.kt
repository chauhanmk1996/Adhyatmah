package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentBlogDetailsBinding
import com.app.adhyatmah.utils.base.BaseFragment


class BlogDetailsFragment : BaseFragment<FragmentBlogDetailsBinding>() {
    override fun setLayout(): Int {
        return R.layout.fragment_blog_details
    }

    override fun initView(savedInstanceState: Bundle?) {

        val title = arguments?.getString("title")
        val content = arguments?.getString("content")
        val imageUrl = arguments?.getString("imageUrl")

        binding.backImg.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.title.text = title
        binding.description.text = content
        Glide.with(requireContext())
            .load(imageUrl)
            .into(binding.blogImg)

    }

}