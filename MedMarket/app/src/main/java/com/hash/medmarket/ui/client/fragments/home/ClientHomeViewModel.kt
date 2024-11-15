package com.hash.medmarket.ui.client.fragments.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.database.model.Store
import com.hash.medmarket.database.model.Users

class ClientHomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _stores = MutableLiveData<List<Users>>()
    val stores: LiveData<List<Users>>
        get() = _stores

    private val _medicines = MutableLiveData<List<Medicine>>()
    val medicines: LiveData<List<Medicine>>
        get() = _medicines

    private val _filteredMedicines = MutableLiveData<List<Medicine>>()
    val filteredMedicines: LiveData<List<Medicine>>
        get() = _filteredMedicines



    fun allStores() {
        firestore.collection("Users")
            .whereEqualTo("userType","pharmacist")
//            .orderBy("timestamp" , Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val storeList = mutableListOf<Users>()
                for (document in documents) {
                    val store = document.toObject(Users::class.java)
                    storeList.add(store)
                    Log.i("Users", "${store}")
                }
                _stores.value = storeList
            }
            .addOnFailureListener { exception ->
                Log.e("ClientHomeViewModel", "Error getting stores", exception)
            }
    }

    fun fetchAllMedicines() {
        firestore.collection("Medicine")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { medicineDocuments ->
                val medicineList = mutableListOf<Medicine>()

                for (medicineDocument in medicineDocuments) {
                    val medicine = medicineDocument.toObject(Medicine::class.java)
                    medicineList.add(medicine)
                }

                _medicines.value = medicineList
            }
            .addOnFailureListener { exception ->
                Log.e("ClientHomeViewModel", "Error getting medicines", exception)
            }
    }


    fun searchMedicine(query: String) {
        val filteredList = if (query.isNotEmpty()) {
            medicines.value?.filter { medicine ->
                medicine.name!!.contains(query, true)
            }
        } else {
            medicines.value
        }
        _filteredMedicines.value = filteredList!!
    }


}

