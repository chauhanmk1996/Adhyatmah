package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.ManageAddItemContainerBinding
import com.app.adhyatmah.domain.model.profile.manage_address.Addresse

class ManageAddressAdapter(
    private val items: MutableList<Addresse>,
    private val onItemClick: (String, Addresse, String, Addresse) -> Unit,

    ) : RecyclerView.Adapter<ManageAddressAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ManageAddItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ManageAddItemContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position]
        holder.binding.nameTv.text = item.name
        val address =
            item.address1 + " " + item.address2 + "\n" + item.city + " " + item.zip + "\n" + item.phone
        holder.binding.addressTv.text = address

        holder.binding.editBtn.setOnClickListener {
            item.id?.let { it1 -> onItemClick(it1, item, "edit", item) }
        }

        holder.binding.deleteBtn.setOnClickListener {
            item.id?.let { it1 -> onItemClick(it1, item, "delete", item) }
        }

        holder.binding.offLayout.setOnClickListener {
            item.id?.let { it1 -> onItemClick(it1, item, "mainLayoutClick", item) }
        }
    }

    override fun getItemCount() = items.size
}