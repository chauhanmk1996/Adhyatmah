package com.app.adhyatmah.presentation.utils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R

class IndicatorRecyclerViewRating @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {

    private val indicatorAdapter = IndicatorAdapter()
    private var currentPosition = 0
    private var indicatorSize = 7.dpToPx()
    private var indicatorSpacing = 4.dpToPx() // Adjust spacing as needed

    init {
        adapter = indicatorAdapter
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        addItemDecoration(IndicatorItemDecoration(indicatorSize, indicatorSpacing))
        setHasFixedSize(true)
    }

    fun setIndicators(count: Int) {
        indicatorAdapter.items = List(count) { it }
        indicatorAdapter.notifyDataSetChanged()
    }

    fun setCurrentPosition(position: Int) {
        val previousPosition = currentPosition
        currentPosition = position
        indicatorAdapter.notifyItemChanged(previousPosition)
        indicatorAdapter.notifyItemChanged(currentPosition)
    }

    private inner class IndicatorAdapter : Adapter<IndicatorAdapter.IndicatorViewHolder>() {
        var items = emptyList<Int>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorViewHolder {
            val view = View(parent.context).apply {
                layoutParams = LayoutParams(indicatorSize, indicatorSize)
            }
            return IndicatorViewHolder(view)
        }

        override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {
            holder.bind(position == currentPosition)
        }

        override fun getItemCount() = items.size

        inner class IndicatorViewHolder(view: View) : ViewHolder(view) {
            fun bind(isSelected: Boolean) {
                itemView.background = if (isSelected) {
                    ContextCompat.getDrawable(context, R.drawable.selected_tab)
                } else {
                    ContextCompat.getDrawable(context, R.drawable.unselected_tab)
                }
            }
        }
    }

    private class IndicatorItemDecoration(
        private val indicatorSize: Int,
        private val spacing: Int,
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: State,
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position != 0) {
                outRect.left = spacing
            }
        }
    }
}