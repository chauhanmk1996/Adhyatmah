package com.app.adhyatmah.presentation.ui.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.BagLayoutListBinding
import com.app.adhyatmah.domain.model.bag_response.get_cart_list_data.Edge

class BagAdapter(
    private var bagItems: MutableList<Edge>,
    private val onQuantityChangeListener: OnQuantityChangeListener
) : RecyclerView.Adapter<BagAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: BagLayoutListBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BagLayoutListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = bagItems[position]
        with(holder.binding) {

            // Safely access the merchandise data
            val merchandise = item.node?.merchandise
            val product = merchandise?.product
            greyTv.text = product?.title
            priceTv.text = "${merchandise?.price?.currencyCode} ${merchandise?.price?.amount}"
            sizeTv.text = "Size: ${merchandise?.title}"
            countTv.text = item.node?.quantity.toString()

            // Load image using Glide (handle null values)
            val img = product?.images?.edges
            Glide.with(modelPic.context)
                .load(img?.get(0)?.node?.url)
                .into(modelPic)

            // Handle plus button click
            plusId.setOnClickListener {
                item.node?.let { node ->
                    val currentQuantity = node.quantity ?: 0
                    val newQuantity = currentQuantity + 1
                    node.quantity = newQuantity
                    holder.binding.countTv.text = newQuantity.toString()

                    onQuantityChangeListener.onQuantityChanged(
                        node.merchandise?.id ?: "",
                        newQuantity, true
                    )
                }
            }
            itemView.setOnLongClickListener {
                val item =    bagItems[position] // Get the current item
                showDeleteConfirmationDialog(holder,item, position)
                true
            }
            // Handle minus button click
            minusId.setOnClickListener {
                item.node?.let { node ->
                    val currentQuantity = node.quantity ?: 0
                    if (currentQuantity > 1) {
                        val newQuantity = currentQuantity - 1
                        node.quantity = newQuantity
                        holder.binding.countTv.text = newQuantity.toString()

                        onQuantityChangeListener.onQuantityChanged(
                            node.merchandise?.id ?: "",
                            newQuantity, false
                        )
                    } else {
                        // Quantity is 1 → remove item
                        bagItems.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, bagItems.size)

                        node.merchandise?.id?.let {
                            onQuantityChangeListener.onQuantityChanged(it, 0, false) // Signal to backend for deletion
                        }

                        // Notify fragment adapter is now empty
                        if (bagItems.isEmpty()) {
                            onQuantityChangeListener.onBagEmpty()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return bagItems.size
    }
    fun updateBagItems(newItems: List<Edge>) {
        bagItems.clear()
        bagItems.addAll(newItems)
        notifyDataSetChanged()
    }

    interface OnQuantityChangeListener {
        fun onQuantityChanged(productId: String, newQuantity: Int, isPlus: Boolean = false)
        fun onBagEmpty()
    }


    private fun showDeleteConfirmationDialog(holder: ViewHolder,item: Edge, position: Int) {
        val context =  holder.itemView.context
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are you sure?")
        builder.setMessage("Do you really want to remove this item from your cart?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            removeItemAt(position, item) // Call the function to remove the item
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog if the user cancels
        }
        builder.show()
    }
    private fun removeItemAt(position: Int, item: Edge) {
        // Remove the item from the list
        bagItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, bagItems.size)

        // Notify the listener that the item was removed
        item.node?.merchandise?.id?.let {
            onQuantityChangeListener.onQuantityChanged(it, 0, false) // Signal backend for deletion
        }

        // Check if the bag is empty after the removal
        if (bagItems.isEmpty()) {
            onQuantityChangeListener.onBagEmpty()
        }
    }


}
