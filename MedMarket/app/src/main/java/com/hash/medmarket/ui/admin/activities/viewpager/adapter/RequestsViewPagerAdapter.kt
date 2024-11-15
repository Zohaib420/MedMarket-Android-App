package com.hash.medmarket.ui.admin.activities.viewpager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hash.medmarket.ui.admin.activities.viewpager.fragments.approved.ApprovedFragment
import com.hash.medmarket.ui.admin.activities.viewpager.fragments.pending.PendingFragment
import com.hash.medmarket.ui.admin.activities.viewpager.fragments.rejected.RejectedFragment

class RequestsViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {

        var getFragment: Fragment? = null
        when(position){
            0 -> getFragment = PendingFragment()
            1 -> getFragment = ApprovedFragment()
            2 -> getFragment = RejectedFragment()
        }

        return getFragment!!
    }

}