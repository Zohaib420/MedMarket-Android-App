package com.hash.medmarket.ui.pharmacists.fragments.extras.store.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hash.medmarket.ui.pharmacists.fragments.extras.store.medicine.MedicineFragment
import com.hash.medmarket.ui.pharmacists.fragments.orders.AllOrdersFragment

class StorePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {

        var getFragment: Fragment? = null
        when(position){
            0 -> getFragment = MedicineFragment()
            1 -> getFragment = AllOrdersFragment()
        }

        return getFragment!!
    }

}