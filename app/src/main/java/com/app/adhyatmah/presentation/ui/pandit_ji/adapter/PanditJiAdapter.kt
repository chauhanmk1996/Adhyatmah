package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.RecyclerPanditJiListBinding
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.Vendor
import com.bumptech.glide.Glide

class PanditJiAdapter(var list: MutableList<Vendor>, var imgClick :(Int)-> Unit, var booking :(Vendor) -> Unit) : RecyclerView.Adapter<PanditJiAdapter.ViewHolder>() {
    inner class ViewHolder(var binding : RecyclerPanditJiListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var binding = RecyclerPanditJiListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.etUserName.text = list[position].firstName + " " + list[position].lastName
        holder.binding.etAddress.text = list[position].city.toString()
//        holder.binding.etStatePincode.text = list[position].
        holder.binding.userImage.setOnClickListener {
            imgClick(position)
        }
        val formattedLanguages = list[position].language?.joinToString(", ") { lang ->
            lang.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ?: "--"
//        holder.binding.etLanguage.text = formattedLanguages

        Glide.with(holder.binding.userImage.context)
            .load(list[position].image?.url)
            .placeholder(R.drawable.pamdit_ji)
            .error(R.drawable.pamdit_ji)
            .into(holder.binding.userImage)


        holder.binding.root.setOnClickListener {
            booking(list[position])

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}