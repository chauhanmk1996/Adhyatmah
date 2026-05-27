package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.OnboardingLayoutBinding
import com.app.adhyatmah.domain.model.auth.GetLandingPageResponse

class OnboardingAdapter(
    private val items: List<GetLandingPageResponse.LandingPage>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {


    inner class OnboardingViewHolder(val binding: OnboardingLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = OnboardingLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = items[position]
//        holder.binding.image.setImageResource(item.image)
        holder.binding.title.text = item.title
        holder.binding.description.text = "Lets Go"

        Glide.with(holder.itemView.context)
            .load(item.landing_page_image_url)
            .into(holder.binding.image)

//        Log.d("TAG", "onBindViewHolder: ${item.discription}")

    }

    override fun getItemCount() = items.size

}
