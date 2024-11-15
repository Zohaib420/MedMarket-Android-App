package com.hash.medmarket.ui.pharmacists.fragments.medicine.allMedicine

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hash.medmarket.database.model.Medicine

class MedicineViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _medicines = MutableLiveData<List<Medicine>>()
    val medicines: LiveData<List<Medicine>>
        get() = _medicines

    private val _deleteMedicineResult = MutableLiveData<Boolean>()
    val deleteMedicineResult: LiveData<Boolean>
        get() = _deleteMedicineResult

    fun getMedicine(storeId: String) {
        firestore.collection("Medicine")
            .whereEqualTo("storeId", storeId)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val medicineList = mutableListOf<Medicine>()
                for (document in documents) {
                    val medicine = document.toObject(Medicine::class.java)
                    medicineList.add(medicine)
                }
                _medicines.value = medicineList
            }
            .addOnFailureListener { exception ->
                Log.d("Time stamp" ,"error : $exception")

            }
    }

    fun deleteMedicine(medicineId: String) {
        firestore.collection("Medicine")
            .document(medicineId)
            .delete()
            .addOnSuccessListener {
                _deleteMedicineResult.value = true
            }
            .addOnFailureListener { exception ->
                _deleteMedicineResult.value = false
            }
    }

}
