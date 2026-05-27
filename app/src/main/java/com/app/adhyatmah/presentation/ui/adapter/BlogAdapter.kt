package com.app.adhyatmah.presentation.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.HomeBlogRecyclerviewBinding
import com.app.adhyatmah.domain.model.home_blog_response.Node

class BlogAdapter(
    var data: List<Node>,
    private val readMoreListener: OnBlogReadMoreClickListener

) :RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: HomeBlogRecyclerviewBinding):RecyclerView.ViewHolder(binding.root)

    var expandedList = MutableList(data.size) { false }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = data[position].image.url
        Log.d("BlogAdapter", "Image URL: $imageUrl")

        val isExpanded = expandedList[position]

        holder.binding.header.text = data[position].title
         holder.binding.description.text = data[position].content
         Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.binding.img)

        // Control visibility or lines
        if (isExpanded) {
            holder.binding.description.maxLines = Int.MAX_VALUE
            holder.binding.readMore.text = "Read Less"
        } else {
            holder.binding.description.maxLines = 3 // or whatever limit
            holder.binding.readMore.text = "Read More"
        }

        holder.binding.readMore.setOnClickListener {
            expandedList[position] = !expandedList[position]
            notifyItemChanged(position)

            if (expandedList[position]) {
                val title = data[position].title
                val content = data[position].content
                val imageUrl = data[position].image.url

                readMoreListener.onReadMoreClicked(title, content, imageUrl)
            }
        }


/*
        holder.binding.readMore.setOnClickListener {
            expandedList[position] = !expandedList[position]
            notifyItemChanged(position)

        }
*/

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = HomeBlogRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    interface OnBlogReadMoreClickListener {
        fun onReadMoreClicked(title: String, content: String, imageUrl: String)
    }


}