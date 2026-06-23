package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ProfileItemContainerBinding

class ProfileAdapter(
    private val profileItems: List<Pair<String, Int>>,
    private val onItemClick: (String) -> Unit,
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    inner class ProfileViewHolder(private val binding: ProfileItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<String, Int>, isLastItem: Boolean) {
            val context = itemView.context
            val redColor = ContextCompat.getColor(itemView.context, R.color.red_D43E29)
            val defaultColor = ContextCompat.getColor(context, R.color.black_272727)

            when (item.first) {
                "Select Language" -> {
                    binding.wishlistTv.setTextColor(defaultColor)
                }

                "Logout", "Delete account" -> {
                    binding.wishlistTv.setTextColor(redColor)
                }

                else -> {
                    binding.wishlistTv.setTextColor(defaultColor)
                }
            }

            binding.view.visibility = if (isLastItem) View.GONE else View.VISIBLE

            binding.wishlistTv.text = item.first
            binding.wishlistImg.setImageResource(item.second)

            binding.root.setOnClickListener {
                onItemClick(item.first)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ProfileItemContainerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val isLastItem = position == profileItems.size - 1
        holder.bind(profileItems[position], isLastItem)
    }

    override fun getItemCount(): Int = profileItems.size
}