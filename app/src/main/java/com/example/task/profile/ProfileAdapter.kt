package com.example.task.profile

import ApplicationsFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tabs.SettingsFragment

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ApplicationsFragment()
            1 -> SettingsFragment()
            else -> throw IllegalStateException("Invalid tab position")
        }
    }
}
