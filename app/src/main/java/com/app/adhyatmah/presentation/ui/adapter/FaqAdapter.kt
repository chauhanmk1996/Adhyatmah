package com.app.adhyatmah.presentation.ui.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemFaqBinding
import com.app.adhyatmah.domain.model.create_booking.Faq
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show

class FaqAdapter(
    private val faqList: ArrayList<Faq>, private val onSelected: (Int, Boolean) -> Unit,
) : BaseRecyclerAdapter<ListItemFaqBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_faq

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun bind(binding: ListItemFaqBinding, position: Int) {
        binding.apply {
            val faq = faqList[position]

            tvQuestion.text = faq.question ?: ""
            tvAnswer.text = faq.answer ?: ""

            if (faq.isOpen == true) {
                clFaq.setBackgroundResource(R.drawable.round_12)
                ivArrow.setImageResource(R.drawable.arrow_up)
                tvAnswer.show()
            } else {
                clFaq.setBackgroundResource(R.drawable.round_12_gray)
                ivArrow.setImageResource(R.drawable.arrow_down)
                tvAnswer.hide()
            }

            clFaq.setOnClickListener {
                val isOpen = faq.isOpen ?: false
                onSelected(position, !isOpen)
            }
        }
    }

    fun openCloseFaq(pos: Int, isOpen: Boolean) {
        faqList[pos].isOpen = isOpen
        notifyItemChanged(pos)
    }
}