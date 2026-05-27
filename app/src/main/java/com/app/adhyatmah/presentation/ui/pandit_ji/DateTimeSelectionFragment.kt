package com.app.adhyatmah.presentation.ui.pandit_ji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentDateTimeSelectionBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.TimeSlotAdapter
import java.text.SimpleDateFormat
import java.util.*

class DateTimeSelectionFragment : Fragment() {

    private lateinit var binding: FragmentDateTimeSelectionBinding
    private lateinit var timeAdapter: TimeSlotAdapter
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDateTimeSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupCalendar()
        setupListeners()
    }

    private fun setupCalendar() {
        // Disable past dates
        binding.calendarView.minDate = System.currentTimeMillis() - 1000

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Default date = today
        selectedDate = dateFormat.format(Date())
        refreshSlots(selectedDate!!)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            if (calendar.timeInMillis < System.currentTimeMillis() - 86400000) {
                Toast.makeText(requireContext(), "You can't select a past date", Toast.LENGTH_SHORT).show()
                return@setOnDateChangeListener
            }

            selectedDate = dateFormat.format(calendar.time)
            refreshSlots(selectedDate!!)
        }
    }

    private fun setupRecycler() {
        binding.rvTimeSlots.layoutManager = LinearLayoutManager(requireContext())
        timeAdapter = TimeSlotAdapter { selected ->
            selectedTime = selected
        }
        binding.rvTimeSlots.adapter = timeAdapter
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            if (selectedDate == null) {
                Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedTime == null) {
                Toast.makeText(requireContext(), "Please select a time slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Combine date and time into ISO format
            val isoDateTime = convertToIsoFormat(selectedDate!!, selectedTime!!)

//            Toast.makeText(requireContext(), "Selected: $isoDateTime", Toast.LENGTH_LONG).show()
            UserPreference.panditjiBookingRequest.dateTime = isoDateTime
            findNavController().navigate(R.id.selectLanguageFragment)
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun refreshSlots(date: String) {
        val slots = mutableListOf<String>()
        var hour = 9
        var minute = 0

        while (hour < 21 || (hour == 21 && minute == 0)) {
            val amPm = if (hour < 12) "AM" else "PM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val time = String.format("%d:%02d %s", displayHour, minute, amPm)
            slots.add(time)

            minute += 30
            if (minute == 60) {
                minute = 0
                hour++
            }
        }

        timeAdapter.updateSlots(slots, date)
    }

    private fun convertToIsoFormat(date: String, time: String): String {
        try {
            // date -> "yyyy-MM-dd"
            // time -> "10:30 AM"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val combined = "$date $time"
            val parsedDate = inputFormat.parse(combined)
            return outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}
