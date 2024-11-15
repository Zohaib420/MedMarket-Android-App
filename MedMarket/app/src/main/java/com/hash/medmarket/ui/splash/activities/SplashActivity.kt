package com.hash.medmarket.ui.splash.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.ActivitySplashBinding
import com.hash.medmarket.ui.admin.activities.AdminActivity
import com.hash.medmarket.ui.auth.activities.AuthActivity
import com.hash.medmarket.ui.auth.activities.PendingPharmaActivity
import com.hash.medmarket.ui.client.activities.MainClientActivity
import com.hash.medmarket.ui.pharmacists.activities.MainPharmacistActivity
import com.hash.medmarket.utils.UserAuthManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCollectors()
    }

    private fun initCollectors() {

        // For Checking if user exists
        if (FirebaseAuth.getInstance().currentUser != null) {

            FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .get().addOnSuccessListener {
                    if (it != null && it.exists()) {

                        val user = it.toObject(Users::class.java)

                        UserAuthManager.saveUserData(this@SplashActivity, user!!)

                        // For Pharmacist
                        if (it.get("userType").toString() == "pharmacist") {
                            val status = it.get("status").toString()
                            if (status == "pending" || status == "" || status == "rejected") {
                                startActivity(
                                    Intent(
                                        this@SplashActivity, PendingPharmaActivity::class.java
                                    ).putExtra("status", status).putExtra("password",it.get("password").toString())
                                )
                                finishAffinity()
                            } else if(status == "approved")  {
                                Intent(this, MainPharmacistActivity::class.java).apply {
                                    startActivity(this)
                                    finishAffinity()
                                }
                            }
                        }
                        // For Admin
                        else if (it.get("userType").toString() == "admin") {
                            Intent(this, AdminActivity::class.java).apply {
                                startActivity(this)
                                finishAffinity()
                            }
                        }

                        // For Normal USer
                        else {
                            Intent(this, MainClientActivity::class.java).apply {
                                startActivity(this)
                                finishAffinity()
                            }
                        }



                    } else {
                        Intent(this, AuthActivity::class.java).apply {
                            startActivity(this)
                            finishAffinity()
                        }
                    }
                }
        } else {
            Intent(this, AuthActivity::class.java).apply {
                startActivity(this)
                finishAffinity()
            }
        }
    }

}