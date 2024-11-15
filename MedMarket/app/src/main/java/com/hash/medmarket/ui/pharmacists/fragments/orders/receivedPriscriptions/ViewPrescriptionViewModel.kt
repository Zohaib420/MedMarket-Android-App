package com.hash.medmarket.ui.pharmacists.fragments.orders.receivedPriscriptions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hash.medmarket.database.model.Order
import com.hash.medmarket.database.model.Prescription

class ViewPrescriptionViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _prescriptions = MutableLiveData<List<Prescription>>()
    val prescriptions: LiveData<List<Prescription>>
        get() = _prescriptions


    fun allOrders(ownerId: String) {
        firestore.collection("Prescription")
            .whereEqualTo("pharmacistId", ownerId)
            .orderBy("timestamp" , Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val medicineList = mutableListOf<Prescription>()
                for (document in documents) {
                    val medicine = document.toObject(Prescription::class.java)
                    medicineList.add(medicine)
                }
                _prescriptions.value = medicineList
            }
            .addOnFailureListener { exception ->
                Log.d("Time stamp" ,"error : $exception")

            }
    }


    fun updateOrderStatusToAvailable(order: Prescription, onComplete: (Boolean) -> Unit) {
        val orderId = order.prescriptionId ?: return
        firestore.collection("Prescription")
            .document(orderId)
            .update("status", "available")
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                onComplete(false)
            }
    }
    fun updateOrderStatusToReject(order: Prescription, onComplete: (Boolean) -> Unit) {
        val orderId = order.prescriptionId ?: return
        firestore.collection("Prescription")
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
