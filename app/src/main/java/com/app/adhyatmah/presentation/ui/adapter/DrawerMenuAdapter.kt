package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.DrawableRecyclerBinding
import com.app.adhyatmah.domain.model.home_menu_response.Item

class DrawerMenuAdapter(
    var menuItem: List<Item>,
    private val onItemClick: (Item) -> Unit,

    ) : RecyclerView.Adapter<DrawerMenuAdapter.ViewHolder>() {
    class ViewHolder(var binding: DrawableRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DrawableRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = menuItem[position]
        val trimmedTitle = items.title.trim()
        holder.binding.text.text = trimmedTitle
        holder.binding.root.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(items)
            }
        }
    }
}