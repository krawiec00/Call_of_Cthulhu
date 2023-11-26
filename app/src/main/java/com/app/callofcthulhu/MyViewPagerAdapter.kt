package com.app.callofcthulhu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.callofcthulhu.fragments.BasicInfoFragment
import com.app.callofcthulhu.fragments.EqFragment
import com.app.callofcthulhu.fragments.StatsFragment
import com.app.callofcthulhu.fragments.TraitFragment


class MyViewPagerAdapter( fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BasicInfoFragment()
            1 -> TraitFragment()
            2 -> StatsFragment()
            3 -> EqFragment()
            else -> BasicInfoFragment()
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}