package com.app.adhyatmah.presentation.ui.pandit_ji.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ItemTimeSlotBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimeSlotAdapter(
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeViewHolder>() {

    private var selectedPosition = -1
    private var slots = listOf<String>()
    private var selectedDate: String? = null

    fun updateSlots(newSlots: List<String>, date: String? = null) {
        slots = newSlots
        selectedDate = date
        selectedPosition = -1
        notifyDataSetChanged()
    }

    inner class TimeViewHolder(private val binding: ItemTimeSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(time: String, position: Int) {
            val isSelected = position == selectedPosition
            val isPast = isPastSlot(time)

            binding.tvTime.text = time

            // Background color for selected/unselected
            binding.cardTime.setCardBackgroundColor(
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
            binding.cardTime.alpha = if (isPast) 0.4f else 1.0f
            binding.root.isEnabled = !isPast

            binding.root.setOnClickListener {
                if (!isPast) {
                    selectedPosition = position
                    onSelect(time)
                    notifyDataSetChanged()
                }
            }
        }

        /** Checks if given time is in the past if date == today */
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeViewHolder(binding)
    }

    override fun getItemCount(): Int = slots.size

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(slots[position], position)
    }
}

