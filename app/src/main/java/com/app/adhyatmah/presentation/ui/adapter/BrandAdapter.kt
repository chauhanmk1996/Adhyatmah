package com.app.adhyatmah.presentation.ui.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.DataString
import com.app.adhyatmah.databinding.BrandRecyclerViewBinding

class BrandAdapter(
    var brandList: List<DataString>,
    private val onBrandClick: (List<DataString>) -> Unit,
) : RecyclerView.Adapter<BrandAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: BrandRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val selectedTextSize = 6f
        val unselectedTextSize = 5f

        val context = binding.root.context

        fun bind(colorItem: DataString) {

            val selectedTextSizeInPx = selectedTextSize * context.resources.displayMetrics.density
            val unselectedTextSizeInPx =
                unselectedTextSize * context.resources.displayMetrics.density

            binding.textId.typeface = if (colorItem.isSelect) {
                Typeface.create("sans-serif-playfair", Typeface.BOLD) // "Playfair-Bold" font
            } else {
                Typeface.create("sans-serif-playfair", Typeface.NORMAL) // "Playfair-Regular" font
            }

            binding.textId.text = colorItem.title

            binding.textId.textSize =
                if (colorItem.isSelect) selectedTextSizeInPx else unselectedTextSizeInPx

            binding.textId.setTextColor(
                if (colorItem.isSelect)
                    ContextCompat.getColor(context, R.color.black)
                else
                    ContextCompat.getColor(
                        context,
                        R.color.black_222222
                    )
            )

            binding.imgId.setBackgroundResource(
                if (colorItem.isSelect) {
                    R.mipmap.selected
                } else R.mipmap.un_selected
            )

            binding.root.setOnClickListener {
                colorItem.isSelect = !colorItem.isSelect

                if (colorItem.isSelect) {
                    selectedColors.add(colorItem)
                } else {
                    selectedColors.removeAll { it.title == colorItem.title }
                }
                onBrandClick(selectedColors)
                notifyDataSetChanged()
            }
        }
    }

    private val selectedColors = mutableListOf<DataString>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BrandRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(brandList[position])
    }

    override fun getItemCount(): Int {
        return brandList.size
    }
}