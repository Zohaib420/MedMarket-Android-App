package com.hash.medmarket.ui.admin.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.hash.medmarket.R
import com.hash.medmarket.databinding.ActivityAdminBinding
import com.hash.medmarket.ui.admin.activities.viewpager.adapter.RequestsViewPagerAdapter
import com.hash.medmarket.ui.auth.activities.AuthActivity
import com.hash.medmarket.utils.UserAuthManager

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var pagerAdapter: RequestsViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            UserAuthManager.clearUserData(this)
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        initPagerAdapter()
        initViewPager()

    }

    private fun initPagerAdapter() {
        pagerAdapter = RequestsViewPagerAdapter(supportFragmentManager)
    }
    private fun initViewPager() {
        binding.requestViewPager.apply {
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
                        getString(R.string.pending)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.blue))
                        setTextColor(Color.WHITE)
                    }
                }
                if (i == 1) customTabView.findViewById<TextView>(R.id.tab_item_text).text =
                    getString(R.string.approved)
                if (i == 2) customTabView.findViewById<TextView>(R.id.tab_item_text).text =
                    getString(R.string.rejected)


            }
        }
        binding.requestViewPager.setCurrentItem(0,true)

    }
}