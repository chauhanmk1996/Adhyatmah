package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ManageAddItemContainerBinding
import com.app.adhyatmah.domain.model.AllCategoryListResponse
import com.app.adhyatmah.domain.model.profile.manage_address.Addresse

class ManageAddressAdapter(
    private val items: MutableList<Addresse>,
    private val onItemClick: (String,Addresse,String, Addresse) -> Unit

    ) : RecyclerView.Adapter<ManageAddressAdapter.CategoryViewHolder>() {
          var selectPosition=0
          var isSelect=false



        inner class CategoryViewHolder(val binding: ManageAddItemContainerBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val binding =
                ManageAddItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CategoryViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            val item = items[position]
            holder.binding.nameTv.text = item.name
            holder.binding.addressTv.text = item.address1+" "+item.address2+"\n"+item.city+" "/*+item.province+"\n"+item.country+" "*/+item.zip+"\n"+item.phone

//              if(selectPosition==position){
//
//                  holder.binding.setDefaultBtn.text="Default"
//                  holder.binding.greyTickBtn.setBackgroundResource(R.drawable.tick_pink)
//              }else{
//                  holder.binding.setDefaultBtn.text="Set as Default"
//                  holder.binding.greyTickBtn.setBackgroundResource(R.drawable.tick_default)
//              }
//            holder.binding.greyTickBtn.setOnClickListener {
//
//                val previousSelected = selectPosition
//                selectPosition = if (selectPosition == position)
//                    -1
//                else
//                    position
//                notifyItemChanged(previousSelected)
//                notifyItemChanged(position)
//
//            }
            holder.binding.editBtn.setOnClickListener {
                item.id?.let { it1 -> onItemClick(it1,item,"edit", item) }

            }
            holder.binding.deleteBtn.setOnClickListener {
                item.id?.let { it1 -> onItemClick(it1,item,"delete", item) }

            }
            holder.binding.offLayout.setOnClickListener {
                item.id?.let { it1 -> onItemClick(it1,item,"mainLayoutClick", item) }
            }

        }

        override fun getItemCount() = items.size

        interface OnItemClickListener {
            fun onItemClick(item: AllCategoryListResponse.Collection)
        }

    }