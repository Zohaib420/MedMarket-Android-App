package com.hash.medmarket.ui.auth.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.databinding.ActivityPendingPharmBinding
import com.hash.medmarket.notifications.NotificationSender
import com.hash.medmarket.ui.pharmacists.activities.MainPharmacistActivity

class PendingPharmaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingPharmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingPharmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkStatus()
        setClicks()
    }

    @SuppressLint("SetTextI18n")
    private fun checkStatus() {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            FirebaseAuth.getInstance().currentUser!!.email!!,
            intent.getStringExtra("password").toString()
        )
            .addOnSuccessListener {
                if (!FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                    binding.tvTitle.text = "Credentials not verified"
                    binding.detail.text = "Please confirm your email\n to continue"
                } else {
                    if (intent.hasExtra("status")) {
                        if (intent.getStringExtra("status") == "") {
                            FirebaseFirestore.getInstance().collection("Users")
                                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                                .update("status", "pending")

                            FirebaseFirestore.getInstance().collection("Users")
                                .document("sNJ0AJyKobSYuBNESKiR7GAVhHu2").get()
                                .addOnSuccessListener { documentSnapShot ->
                                    if (documentSnapShot != null && documentSnapShot.exists()) {
                                        val token = documentSnapShot.get("token").toString()

                                        NotificationSender.sendNotification(
                                            "Approval request",
                                            "New Pharmacist Request received",
                                            this, token
                                        )
                                    }
                                }

                        }
                        else if(intent.getStringExtra("status") == "rejected"){
                            binding.tvTitle.text = "Credentials not verified"
                            binding.detail.text = "Your account is rejected"
                        }
                        else if (intent.getStringExtra("status") == "approved") {
                            Intent(this, MainPharmacistActivity::class.java).apply {
                                startActivity(this)
                                finishAffinity()
                            }
                        }
                    }
                }
            }

    }

    private fun setClicks() {
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Intent(this, AuthActivity::class.java).apply {
                startActivity(this)
                finishAffinity()
            }
        }
    }

}