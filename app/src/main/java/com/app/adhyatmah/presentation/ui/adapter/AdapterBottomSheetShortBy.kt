package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.DataString
import com.app.adhyatmah.databinding.RecyclerShortFilterBinding

class AdapterBottomSheetShortBy(
    var data: List<DataString>,
    private var function: (DataString) -> Unit,
) : RecyclerView.Adapter<AdapterBottomSheetShortBy.ViewPagger>() {
    private var selectedPosition = -1

    class ViewPagger(var binding: RecyclerShortFilterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewPagger {
        val binding =
            RecyclerShortFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagger(binding)
    }

    override fun onBindViewHolder(holder: ViewPagger, position: Int) {
        val list = data[position]
        holder.binding.text.text = list.title

        if (selectedPosition == position) {
            holder.binding.layout.background =
                holder.itemView.context.getDrawable(R.drawable.rectangle_black_no_corner)
            holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.white))

        } else {
            holder.binding.layout.background = null
            holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.black_222222))

        }
        holder.binding.layout.setOnClickListener {
            val previsousClick = selectedPosition
            selectedPosition = position
            notifyItemChanged(previsousClick)
            notifyItemChanged(selectedPosition)
            function(list)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}