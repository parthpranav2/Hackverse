package com.example.hackverse

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PortfolioPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() : Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RegisteredEventsFragment()
            1 -> InvitationsFragment()
            2 -> PendingRegistrationsFragment()
            3 -> RequestsFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
