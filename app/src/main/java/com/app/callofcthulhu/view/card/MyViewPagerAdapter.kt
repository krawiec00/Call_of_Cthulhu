package com.app.callofcthulhu.view.card

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.callofcthulhu.view.fragments.BasicInfoFragment
import com.app.callofcthulhu.view.fragments.EqFragment
import com.app.callofcthulhu.view.fragments.StatsFragment
import com.app.callofcthulhu.view.fragments.NewStatsFragment
import com.app.callofcthulhu.view.fragments.StoryFragment
import com.app.callofcthulhu.view.fragments.TraitFragment


class MyViewPagerAdapter(fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {
    val id = CardDetailsActivity.docId
    private var isEdited = false
    override fun createFragment(position: Int): Fragment {
        if (id.isNotEmpty()) {
            isEdited = true
        }
        return when (position) {
            0 -> BasicInfoFragment()
            1 -> TraitFragment()
            2 -> {
                if (isEdited) {
                    StatsFragment()
                } else {
                    NewStatsFragment()
                }
            }

            3 -> EqFragment()
            4 -> StoryFragment()
            else -> BasicInfoFragment()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}