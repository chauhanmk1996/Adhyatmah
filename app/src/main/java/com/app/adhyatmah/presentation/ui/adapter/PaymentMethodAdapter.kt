package com.app.adhyatmah.presentation.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.PaymentMethod
import com.app.adhyatmah.databinding.PaymentmethodLayoutListBinding

class PaymentMethodAdapter(var paymentList: MutableList<PaymentMethod>) : Adapter<PaymentMethodAdapter.ViewModel>() {
    inner class ViewModel(var binding: PaymentmethodLayoutListBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodAdapter.ViewModel {
        val binding = PaymentmethodLayoutListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun onBindViewHolder(holder: PaymentMethodAdapter.ViewModel, position: Int) {
        val paymentMethod = paymentList[position]
        holder.binding.cardTv.text = paymentMethod.name
        Glide.with(holder.itemView.context).load(paymentMethod.icon).into(holder.binding.cardImg)

        val icon = if (paymentMethod.isSelected) R.drawable.selected_round_btn else R.drawable.unselected_rounded_btn

        Glide.with(holder.itemView.context).load(icon).into(holder.binding.cardSelectionImg)

       holder.binding.layout.setOnClickListener {
       // Add navigation or action here if needed }
           paymentList.forEachIndexed { index, item ->
               item.isSelected = index == position
           }
           notifyDataSetChanged()

       }
    }
    fun getSelectedPaymentMethod(): PaymentMethod? {
        return paymentList.find { it.isSelected }
    }
    override fun getItemCount(): Int {
        return paymentList.size
    }
}