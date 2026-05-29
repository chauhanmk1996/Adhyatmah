package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import androidx.core.content.ContextCompat
import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemTimeSlotBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimeSlotAdapter(
    private val onSelect: (String) -> Unit,
) : BaseRecyclerAdapter<ListItemTimeSlotBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_time_slot

    private var selectedPosition = -1
    private var timeSlotsList = listOf<String>()
    private var selectedDate: String? = null


    override fun bind(binding: ListItemTimeSlotBinding, position: Int) {
        val isSelected = position == selectedPosition
        val time = timeSlotsList[position]
        val isPast = isPastSlot(time)

        binding.tvTime.text = time

        binding.cvTime.setCardBackgroundColor(
            ContextCompat.getColor(
                binding.root.context,
                if (isSelected) R.color.orange else R.color.white
            )
        )

        // Text color for selected/unselected
        binding.tvTime.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                if (isSelected) R.color.white else R.color.orange
            )
        )

        // ✅ Dim past slots
        binding.cvTime.alpha = if (isPast) 0.4f else 1.0f
        binding.root.isEnabled = !isPast

        binding.root.setOnClickListener {
            if (!isPast) {
                selectedPosition = position
                onSelect(time)
                notifyDataSetChanged()
            }
        }
    }

    private fun isPastSlot(slotTime: String): Boolean {
        if (selectedDate == null) return false

        // Check if selected date is today
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = formatter.format(Date())

        if (selectedDate != todayStr) return false

        // Parse slot time (e.g., "9:30 AM")
        return try {
            val slotFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            val slotDate = slotFormatter.parse(slotTime)

            val calNow = Calendar.getInstance()
            val nowDate = slotFormatter.parse(slotFormatter.format(calNow.time))

            slotDate?.before(nowDate) == true
        } catch (e: Exception) {
            false
        }
    }

    fun updateSlots(newSlots: List<String>, date: String? = null) {
        timeSlotsList = newSlots
        selectedDate = date
        selectedPosition = -1
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return timeSlotsList.size
    }
}

