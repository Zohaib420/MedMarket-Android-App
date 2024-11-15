package com.hash.medmarket.ui.pharmacists.fragments.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.hash.medmarket.database.model.Prescription
import com.hash.medmarket.database.model.Store
import java.util.UUID

class PharmacistHomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val storageRef = Firebase.storage.reference
    private lateinit var imageRef: StorageReference
    private val imageUploadResult = MutableLiveData<Boolean>()

    var store = Store()

    var imageUri: Uri? = null


    private val _stores = MutableLiveData<List<Store>>()
    val stores: LiveData<List<Store>>
        get() = _stores
    private val _deleteStoreResult = MutableLiveData<Boolean>()
    val deleteStoreResult: LiveData<Boolean>
        get() = _deleteStoreResult


    fun saveStore(onComplete: (Boolean) -> Unit) {

        val id = firestore.collection("Stores").document().id
        store.storeId=id
        firestore.collection("Stores")
            .document(id)
            .set(store)
            .addOnSuccessListener { documentReference ->
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }


    fun saveStoreImage(onComplete: (Boolean) -> Unit) {
        imageUri?.let { uri ->
            val imageFileName = UUID.randomUUID().toString()
            imageRef = storageRef.child("images/$imageFileName.jpg")
            val uploadTask = imageRef.putFile(uri)
            uploadTask.addOnSuccessListener { uploadTaskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    store.image = downloadUri.toString()
                    saveStore { success ->
                        if (success) {
                            onComplete(true)
                        } else {
                            imageUploadResult.value = false
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                onComplete(false)
            }
        } ?: run {
            saveStore { success ->
                if (success) {
                    onComplete(true)
                } else {
                    imageUploadResult.value = false
                }
            }
        }
    }


    fun getStoresByOwnerId(ownerId: String) {
        firestore.collection("Stores")
            .whereEqualTo("ownerId", ownerId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val storeList = mutableListOf<Store>()
                for (document in documents) {
                    val store = document.toObject(Store::class.java)
                    storeList.add(store)
                }
                _stores.value = storeList
            }
            .addOnFailureListener { exception ->
                Log.d("Time stamp" ,"error : $exception")
            }
    }

    fun deleteMedicine(storeId: String) {
        firestore.collection("Stores")
            .document(storeId)
            .delete()
            .addOnSuccessListener {
                _deleteStoreResult.value = true
            }
            .addOnFailureListener { exception ->
                _deleteStoreResult.value = false
            }
    }

    fun updateStore(updatedStore: Store, onComplete: (Boolean) -> Unit) {
        firestore.collection("Stores")
            .document(updatedStore.storeId!!)
            .set(updatedStore)
            .addOnSuccessListener { documentReference ->
                Log.e("FirestoreUpdate", "store: ${updatedStore.storeId}")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreUpdate", "Error updating store: $e")
                onComplete(false)
            }
    }




}
