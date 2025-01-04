package com.example.hackverse

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.content.res.Resources // Correct import for Resources

class PortfolioPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() : Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RegisteredEventsFragment()
            1 -> RequestsFragment()
            2 -> PendingRegistrationsFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
