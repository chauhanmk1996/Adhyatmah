package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.ListItemMultilplePujaBinding

class MultiplePoojaAdapter(
    var list: MutableList<String>,
    var onMultipleClick: (selectedListPosition: List<Int>, selectedListItem: List<String>) -> Unit,
) : RecyclerView.Adapter<MultiplePoojaAdapter.ViewHolder>() {
    private var selectedItemsPosition: MutableList<Int> = mutableListOf()
    private var selectedItems: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemMultilplePujaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val isSelected = selectedItemsPosition.contains(position)

        if (isSelected) {
            holder.binding.container.setBackgroundResource(com.app.adhyatmah.R.drawable.rectangle_f5f4f8_stock_theme_1_radius_20)
        } else {
            holder.binding.container.setBackgroundResource(com.app.adhyatmah.R.drawable.rectangle_solid_eeeeee_7_radius)
        }

        holder.binding.textView.text = item
        holder.binding.container.setOnClickListener {
            toggleSelection(position, item)
        }
    }

    fun toggleSelection(position: Int, item: String) {
        if (selectedItemsPosition.contains(position)) {
            selectedItemsPosition.remove(position)
            selectedItems.remove(item)
        } else {
            selectedItemsPosition.add(position)
            selectedItems.add(item)

        }
        onMultipleClick(selectedItemsPosition, selectedItems)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(var binding: ListItemMultilplePujaBinding) :
        RecyclerView.ViewHolder(binding.root)
}