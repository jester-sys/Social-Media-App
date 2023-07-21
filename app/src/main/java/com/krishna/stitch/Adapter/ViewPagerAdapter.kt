package com.krishna.stitch.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.krishna.stitch.ProfileUI.ReplayFragment
import com.krishna.stitch.ProfileUI.StitcherFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StitcherFragment()// Return a new instance of HomeFragment for position 0
            else -> ReplayFragment() // Return a new instance of PlayNextFragment for any other position
        }
    }

    override fun getItemCount(): Int {
        return 2 // Return the total number of fragments managed by the adapter, which is 4 in this case
    }
}
