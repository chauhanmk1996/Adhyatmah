package com.app.adhyatmah.presentation.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.DrawableRecyclerBinding
import com.app.adhyatmah.domain.model.home_menu_response.Item

class DrawerMenuAdapter(
    var menuItem: List<Item>,
    private val onItemClick: (Item) -> Unit

) : RecyclerView.Adapter<DrawerMenuAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: DrawableRecyclerBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = DrawableRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return menuItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = menuItem[position]
        val trimmedTitle = items.title.trim()
        holder.binding.text.text = trimmedTitle

      //  holder.binding.text.text = menuItem[position].title.trim()

        Log.d("TAG", "onBindViewHolder:$items ")
        holder.binding.root.setOnClickListener {
//            val adptrposition = position
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(items)
            }
            Log.d("TAG", "InitView: ${items.collectionHandle}")
        }
    }


}