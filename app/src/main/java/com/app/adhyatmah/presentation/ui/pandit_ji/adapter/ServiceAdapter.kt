package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ItemServiceCardBinding
import com.app.adhyatmah.domain.model.ServiceModel
import com.app.adhyatmah.domain.model.get_services.GetServicesResponse
import com.google.android.material.card.MaterialCardView

class ServiceAdapter(
    private val services: MutableList<GetServicesResponse.Payload.Service>,
    private val onServiceSelected: (GetServicesResponse.Payload.Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    private var selectedPosition = -1

    inner class ServiceViewHolder(val binding: ItemServiceCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ServiceViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        with(holder.binding) {
            tvPoojaType.text = service.poojaType
            tvDescription.text = service.description
            tvPrice.text = service.price.toString()
            tvDuration.text = service.duration

            // Radio Button selection
            rbSelect.isChecked = position == selectedPosition

            // Card border color change
            if (selectedPosition == position) {
                cardService.strokeColor = root.context.getColor(R.color.theme)
                cardService.strokeWidth = 2
            } else {
                cardService.strokeColor = root.context.getColor(R.color.gray)
                cardService.strokeWidth = 1
            }

            root.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onServiceSelected(service)
            }

            rbSelect.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onServiceSelected(service)
            }
        }
    }

    override fun getItemCount(): Int = services.size

    fun updateServices(newServices: MutableList<GetServicesResponse.Payload.Service>) {
        services.clear()
        services.addAll(newServices)
        notifyDataSetChanged()
    }
}

