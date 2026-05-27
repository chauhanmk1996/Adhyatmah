package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

class HelpCenterAdapter: androidx.viewpager2.adapter.FragmentStateAdapter {
    private val fragments: List<androidx.fragment.app.Fragment>
    constructor(fragment: androidx.fragment.app.Fragment, fragments: List<androidx.fragment.app.Fragment>) : super(fragment) {
        this.fragments = fragments
    }
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): androidx.fragment.app.Fragment = fragments[position]
}