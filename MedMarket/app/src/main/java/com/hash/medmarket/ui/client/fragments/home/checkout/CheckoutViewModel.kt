package com.hash.medmarket.ui.client.fragments.home.checkout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.database.model.Order
import com.hash.medmarket.database.model.Store
import com.hash.medmarket.database.model.Users

class CheckoutViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    var order= Order()

    fun saveOrder(onComplete: (Boolean) -> Unit) {

        val id = firestore.collection("Orders").document().id
        order.orderId=id
        firestore.collection("Orders")
            .document(id)
            .set(order)
            .addOnSuccessListener { documentReference ->
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }

}

