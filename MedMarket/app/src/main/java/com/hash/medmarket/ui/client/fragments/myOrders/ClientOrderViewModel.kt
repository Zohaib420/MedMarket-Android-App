package com.hash.medmarket.ui.client.fragments.myOrders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.database.model.Order

class ClientOrderViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>>
        get() = _orders


    fun allOrders(userId: String) {
        firestore.collection("Orders")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val medicineList = mutableListOf<Order>()
                for (document in documents) {
                    val medicine = document.toObject(Order::class.java)
                    medicineList.add(medicine)
                }
                _orders.value = medicineList
            }
            .addOnFailureListener { exception ->
                Log.e("ClientHomeViewModel", "Error getting medicines", exception)
            }
    }

}
