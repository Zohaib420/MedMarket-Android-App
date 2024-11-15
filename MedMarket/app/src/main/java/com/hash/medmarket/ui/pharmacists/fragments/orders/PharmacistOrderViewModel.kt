package com.hash.medmarket.ui.pharmacists.fragments.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hash.medmarket.database.model.Order

class PharmacistOrderViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>>
        get() = _orders


    fun allOrders(ownerId: String) {
        firestore.collection("Orders")
            .whereEqualTo("pharmacistId", ownerId)
            .orderBy("timestamp" , Query.Direction.DESCENDING)
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
                Log.d("Time stamp" ,"error : $exception")

            }
    }


    fun updateOrderStatusToAccepted(order: Order, onComplete: (Boolean) -> Unit) {
        val orderId = order.orderId ?: return
        firestore.collection("Orders")
            .document(orderId)
            .update("status", "accepted")
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                onComplete(false)
            }
    }
    fun updateOrderStatusToRejected(order: Order, onComplete: (Boolean) -> Unit) {
        val orderId = order.orderId ?: return
        firestore.collection("Orders")
            .document(orderId)
            .update("status", "rejected")
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                onComplete(false)
            }
    }


}
