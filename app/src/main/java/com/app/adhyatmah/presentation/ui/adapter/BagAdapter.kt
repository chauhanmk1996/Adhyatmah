package com.app.adhyatmah.presentation.ui.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.BagLayoutListBinding
import com.app.adhyatmah.domain.model.bag_response.get_cart_list_data.Edge

class BagAdapter(
    private var bagItems: MutableList<Edge>,
    private val onQuantityChangeListener: OnQuantityChangeListener,
) : RecyclerView.Adapter<BagAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: BagLayoutListBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BagLayoutListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = bagItems[position]
        with(holder.binding) {
            val merchandise = item.node?.merchandise
            val product = merchandise?.product
            greyTv.text = product?.title
            val amountText = "${merchandise?.price?.currencyCode} ${merchandise?.price?.amount}"
            priceTv.text = amountText
            val context = holder.itemView.context
            val sizeText = "${context.getString(R.string.size_heading)} ${merchandise?.title}"
            sizeTv.text = sizeText
            countTv.text = item.node?.quantity.toString()

            val img = product?.images?.edges
            Glide.with(modelPic.context)
                .load(img?.get(0)?.node?.url)
                .into(modelPic)

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
                val item = bagItems[position]
                showDeleteConfirmationDialog(holder, item, position)
                true
            }

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
                        bagItems.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, bagItems.size)

                        node.merchandise?.id?.let {
                            onQuantityChangeListener.onQuantityChanged(
                                it,
                                0,
                                false
                            )
                        }

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

    private fun showDeleteConfirmationDialog(holder: ViewHolder, item: Edge, position: Int) {
        val context = holder.itemView.context
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(com.app.adhyatmah.R.string.are_you_sure))
        builder.setMessage(context.getString(com.app.adhyatmah.R.string.do_you_really_want_to_remove_this_item_from_your_cart))
        builder.setPositiveButton(context.getString(com.app.adhyatmah.R.string.yes)) { dialog, _ ->
            removeItemAt(position, item)
            dialog.dismiss()
        }
        builder.setNegativeButton(context.getString(com.app.adhyatmah.R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun removeItemAt(position: Int, item: Edge) {
        bagItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, bagItems.size)

        item.node?.merchandise?.id?.let {
            onQuantityChangeListener.onQuantityChanged(it, 0, false)
        }

        if (bagItems.isEmpty()) {
            onQuantityChangeListener.onBagEmpty()
        }
    }
}