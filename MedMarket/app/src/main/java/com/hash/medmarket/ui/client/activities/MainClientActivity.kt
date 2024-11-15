package com.hash.medmarket.ui.client.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hash.medmarket.R
import com.hash.medmarket.databinding.ActivityMainClientBinding
import com.hash.medmarket.databinding.ActivityMainPharmacistBinding
import com.hash.medmarket.ui.auth.activities.AuthActivity
import com.hash.medmarket.ui.pharmacists.activities.PharmacistDrawerListener
import com.hash.medmarket.utils.UserAuthManager

class MainClientActivity : AppCompatActivity(), PharmacistDrawerListener {

    private lateinit var binding: ActivityMainClientBinding


    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainClientBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main_client)

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.client_navigation)

        navController.graph = graph

        navView.setupWithNavController(navController)
        navView.menu.findItem(R.id.drw_logout).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.drw_logout -> {

                    FirebaseAuth.getInstance().signOut()
                    UserAuthManager.clearUserData(this)
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finishAffinity()

                    return@setOnMenuItemClickListener true
                }
                else -> {

                    return@setOnMenuItemClickListener false
                }
            }
        }



    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()

    }


    override fun openDrawer() {
        binding.drawerLayout.open()
    }

    override fun closeDrawer() {
        binding.drawerLayout.close()
    }


}