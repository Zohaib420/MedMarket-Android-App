package com.hash.medmarket.ui.client.fragments.home.medicines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.database.model.Medicine

class ClientMedicineViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _medicines = MutableLiveData<List<Medicine>>()
    val medicines: LiveData<List<Medicine>>
        get() = _medicines

    private val _storeData = MutableLiveData<Map<String, Any>>()
    val storeData: LiveData<Map<String, Any>> = _storeData




    fun getMedicine(storeId: String) {
        firestore.collection("Medicine")
            .whereEqualTo("storeId", storeId)
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
                // Handle failure
            }
    }
    fun fetchAllMedicine() {
        firestore.collection("Medicine")
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
                // Handle failure
            }
    }



    fun getStoreData(storeId: String) {
        firestore.collection("Users")
            .whereEqualTo("userId", storeId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val storeData = documents.documents[0].data
                    _storeData.value = storeData!!
                } else {
                    _storeData.value = emptyMap()
                }
            }
            .addOnFailureListener { exception ->
                _storeData.value = emptyMap()
            }
    }


}
