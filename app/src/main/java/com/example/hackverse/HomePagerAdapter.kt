package com.example.hackverse

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.content.res.Resources // Correct import for Resources

class HomePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() : Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BookmarkFragment()
            1 -> ActiveTeamsFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
