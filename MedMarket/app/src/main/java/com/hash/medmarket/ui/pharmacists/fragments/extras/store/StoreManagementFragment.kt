package com.hash.medmarket.ui.pharmacists.fragments.extras.store

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.hash.medmarket.R
import com.hash.medmarket.databinding.FragmentStoreManagementBinding
import com.hash.medmarket.ui.pharmacists.fragments.extras.store.adapter.StorePagerAdapter

class StoreManagementFragment : Fragment() {

    private lateinit var binding: FragmentStoreManagementBinding

    private lateinit var pagerAdapter: StorePagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreManagementBinding.inflate(inflater, container, false)
        initPagerAdapter()
        initViewPager()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }

    private fun initClickListeners() {


        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }


    }
    private fun initPagerAdapter() {
        pagerAdapter = StorePagerAdapter(childFragmentManager)
    }
    private fun initViewPager() {
        binding.propertyViewPager.apply {
            adapter = pagerAdapter
            offscreenPageLimit = 3

        }.let {
            binding.tabHolderLayout.setupWithViewPager(it)
            binding.tabHolderLayout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    val customTabView = p0?.customView
                    customTabView?.findViewById<TextView>(R.id.tab_item_text)?.apply {
                        setTextColor(Color.WHITE)
                        //   background = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //    setBackgroundResource(R.drawable.tab_icon_unselected)
                            backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.blue))
                        }
                    }

                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                    val customTabView = p0?.customView
                    customTabView?.findViewById<TextView>(R.id.tab_item_text)?.apply {
                        setTextColor(Color.GRAY)
                        //  setBackgroundResource(R.drawable.tab_icon_unselected)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.bt_very_light_gray))
                        }
                    }

                }

                override fun onTabReselected(p0: TabLayout.Tab?) {
                    val customTabView = p0?.customView
                    customTabView?.findViewById<TextView>(R.id.tab_item_text)?.apply {
                        setTextColor(Color.WHITE)
                        //  background = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //    setBackgroundResource(R.drawable.tab_icon_unselected)
                            backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.blue))
                        }
                    }
                }

            })
        }
        for (i in 0 until binding.tabHolderLayout.tabCount) {
            val tab: TabLayout.Tab? = binding.tabHolderLayout.getTabAt(i)
            tab?.setCustomView(R.layout.tab_item_layout)
            val customTabView = tab?.customView
            if (customTabView != null) {
                if (i == 0) customTabView.findViewById<TextView>(R.id.tab_item_text).apply {
                    text =
                        getString(R.string.medicines)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.blue))
                        setTextColor(Color.WHITE)
                    }
                }
                if (i == 1) customTabView.findViewById<TextView>(R.id.tab_item_text).text =
                    getString(R.string.orders)

            }
        }
        binding.propertyViewPager.setCurrentItem(0,true)

    }




}
