package com.app.callofcthulhu.view.card

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.callofcthulhu.view.fragments.BasicInfoFragment
import com.app.callofcthulhu.view.fragments.EqFragment
import com.app.callofcthulhu.view.fragments.StatsFragment
import com.app.callofcthulhu.view.fragments.StatsFragment2
import com.app.callofcthulhu.view.fragments.StoryFragment
import com.app.callofcthulhu.view.fragments.TraitFragment


class MyViewPagerAdapter( fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {

    val id = CardDetailsActivity.docId
    var isEdited = false
    override fun createFragment(position: Int): Fragment {
        if(id.isNotEmpty()){
            isEdited=true
        }
        //Log.d("SPRAWDZAMY ISEDITED","WARTOŚC: $isEdited")
        return when (position) {
            0 -> BasicInfoFragment()
            1 -> TraitFragment()
            2 -> {
                // Tworzenie fragmentu w zależności od warunku if
                if (isEdited) {
                    StatsFragment()
                } else {
                    StatsFragment2()
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