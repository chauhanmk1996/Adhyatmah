package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.RecyclerMultiplePoojaBinding

class MultiplePoojaAdapter(var list: MutableList<String>,
                            var onMultipleClick :(selectedListPosition: List<Int>, selectedListItem: List<String>) -> Unit
) : RecyclerView.Adapter<MultiplePoojaAdapter.ViewHolder>() {
    private var selectedItemsPosition: MutableList<Int> = mutableListOf()
    private var selectedItems: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        var binding = RecyclerMultiplePoojaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       var item  = list[position]
        val isSelected = selectedItemsPosition.contains(position)
        val isSelectedText = item
        if(isSelected){
            holder.binding.container.setBackgroundResource(com.app.adhyatmah.R.drawable.rectangle_f5f4f8_stock_theme_1_radius_20)

        } else{
            holder.binding.container.setBackgroundResource(com.app.adhyatmah.R.drawable.rectangle_solid_eeeeee_7_radius)
        }

        holder.binding.textView.text = item
        holder.binding.container.setOnClickListener {
            toggleSelection(position,item)
        }
    }

    // Toggle selection logic
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

    // Public method to get selected items
    fun getSelectedItems(): List<String> {
        return selectedItemsPosition.map { list[it] }
    }


    // Optional: clear selections
    fun clearSelections() {
        val selectedCopy = selectedItemsPosition.toList()
        selectedItemsPosition.clear()
        selectedCopy.forEach { notifyItemChanged(it) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(var binding : RecyclerMultiplePoojaBinding) : RecyclerView.ViewHolder(binding.root)
}