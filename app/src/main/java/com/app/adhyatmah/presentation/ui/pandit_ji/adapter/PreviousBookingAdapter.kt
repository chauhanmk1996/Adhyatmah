package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.CANCELLED
import com.app.adhyatmah.data.preferences.ON_GOING
import com.app.adhyatmah.data.preferences.PENDING
import com.app.adhyatmah.data.preferences.UP_COMING
import com.app.adhyatmah.databinding.RcvItemBookingBinding
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.bumptech.glide.Glide

class PreviousBookingAdapter(
    private val context: Context,
    var type: String,
    var setList: MutableList<GetBookingResponse.Payload.Booking>,
    var completeBooking: (GetBookingResponse.Payload.Booking) -> Unit,
    var cancelBooking: (GetBookingResponse.Payload.Booking) -> Unit,
    var callBack: (Int) -> Unit,
    var callClick: (String) -> Unit,
) : RecyclerView.Adapter<PreviousBookingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RcvItemBookingBinding.inflate(
            (context as Activity).layoutInflater, parent, false
        )
        return ViewHolder(binding)
    }

    class ViewHolder(var binding: RcvItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = setList[position]

        holder.binding.apply {
            tvBookingId.text = data.bookingID

            val name = data.vendor.firstName + " " + data.vendor.lastName
            btNameId.text = name

            if (data.pujaSamagri == null || (data.pujaSamagri.pujaKit.isNullOrEmpty() && data.pujaSamagri.instantKit.isNullOrEmpty())) {
                tvPujaSamagri.hide()
                tvPujaKit.hide()
                tvInstantKit.hide()
            } else {
                tvPujaSamagri.show()
                if (data.pujaSamagri.pujaKit.isNullOrEmpty()) {
                    tvPujaKit.hide()
                } else {
                    tvPujaKit.show()
                    var pujaKit = ""
                    data.pujaSamagri.pujaKit.forEach { kit ->
                        pujaKit = if (pujaKit.isEmpty()) {
                            "${context.getString(R.string.puja_kit)} - $kit"
                        } else {
                            "$pujaKit, $kit"
                        }
                    }
                    tvPujaKit.text = pujaKit
                }

                if (data.pujaSamagri.instantKit.isNullOrEmpty()) {
                    tvInstantKit.hide()
                } else {
                    tvInstantKit.show()
                    var instantKit = ""
                    data.pujaSamagri.instantKit.forEach { kit ->
                        instantKit = if (instantKit.isEmpty()) {
                            "${context.getString(R.string.instant_kit)} - $kit"
                        } else {
                            "$instantKit, $kit"
                        }
                    }
                    tvInstantKit.text = instantKit
                }
            }

            val address =
                data.address.streetAddress + ", " + data.address.city + ", " + data.address.state + ", " + data.address.country + ", " + data.address.zip
            tvAddressId1.text = address

            tvInr.text = CommonUtils.formatDate(data.dateTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            poojaName.text = data.poojaType

            Glide.with(context).load(data.vendor.image)
                .placeholder(R.drawable.pamdit_ji)
                .error(R.drawable.pamdit_ji)
                .into(ivProf)
        }
        when (type) {
            PENDING -> {
                holder.binding.btStatusId.text = context.getString(R.string.pending)
                holder.binding.btStatusId.backgroundTintList =
                    context.getColorStateList(R.color.red_e30000)
                holder.binding.btnCompleteId.visibility = View.GONE
                holder.binding.btnLayoutIdId.visibility = View.VISIBLE
                holder.binding.btnCancel.visibility = View.VISIBLE
            }

            ON_GOING -> {
                holder.binding.btStatusId.backgroundTintList =
                    context.getColorStateList(R.color.theme)
                holder.binding.btStatusId.text = context.getString(R.string.ongoing)
                holder.binding.btnCompleteId.visibility = View.VISIBLE
                holder.binding.btnLayoutIdId.visibility = View.GONE
            }

            UP_COMING -> {
                holder.binding.btStatusId.text = context.getString(R.string.upcoming)
                holder.binding.btStatusId.backgroundTintList =
                    context.getColorStateList(R.color.green_00da45)
                holder.binding.btnCompleteId.visibility = View.GONE
                holder.binding.btnLayoutIdId.visibility = View.GONE
                holder.binding.btnCancel.visibility = View.VISIBLE
            }

            CANCELLED -> {
                holder.binding.btStatusId.text = context.getString(R.string.cancelled)
                holder.binding.btStatusId.backgroundTintList =
                    context.getColorStateList(R.color.red_e30000)
                holder.binding.btnCompleteId.visibility = View.GONE
                holder.binding.btnLayoutIdId.visibility = View.GONE
                holder.binding.btnCancel.visibility = View.GONE
            }

            else -> {
                holder.binding.btStatusId.text = context.getString(R.string.completed)
                holder.binding.btnCompleteId.visibility = View.GONE
                holder.binding.btnLayoutIdId.visibility = View.GONE
            }
        }

        holder.binding.cardUpcomingContainer.setOnClickListener {
            callBack(position)
        }

        holder.binding.btnCompleteId.setOnClickListener {
            completeBooking(data)
        }

        holder.binding.btnCancel.setOnClickListener {
            cancelBooking(data)
        }

        holder.binding.ivCall.setOnClickListener {
            callClick(data.vendor.phone)
        }
    }

    override fun getItemCount(): Int {
        return setList.size
    }
}