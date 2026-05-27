package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.ApplycouponsLayoutListBinding
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.get_all_apply_coupons.Coupon

class CouponAdapter(
    private var coupons: MutableList<Coupon>,
    private val onApplyCouponsChangeListener: OnApplyCouponsChangeListener


) : RecyclerView.Adapter<CouponAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ApplycouponsLayoutListBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ApplycouponsLayoutListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coupon = coupons[position]
        with(holder.binding) {
            // Set text fields from static data
            offTv.text = coupon.code
            minPurchaseTv.text = coupon.title

            /*val applyButton = root.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(
                R.id.apply_button_layout
            )

            applyButton.setOnClickListener {

            }*/

        }
        holder.binding.applyText.setOnClickListener {

            onApplyCouponsChangeListener.onApplyCouponsChanged(coupon.code)
        }
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    // Update the adapter with new coupons
    fun updateCoupons(newCoupons: List<Coupon>) {
        coupons.clear()
        coupons.addAll(newCoupons)
        notifyDataSetChanged()
    }

    interface OnApplyCouponsChangeListener {
        fun onApplyCouponsChanged(coupons: String)

    }

}