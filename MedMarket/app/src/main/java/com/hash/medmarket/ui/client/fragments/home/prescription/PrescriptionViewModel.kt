package com.hash.medmarket.ui.client.fragments.home.prescription

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.database.model.Order
import com.hash.medmarket.database.model.Prescription
import com.hash.medmarket.database.model.Store
import com.hash.medmarket.database.model.Users
import java.util.UUID

class PrescriptionViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = Firebase.storage.reference
    private lateinit var imageRef: StorageReference
    private val imageUploadResult = MutableLiveData<Boolean>()
    var imageUri: Uri? = null
    var prescription = Prescription()

    private val _prescriptions = MutableLiveData<List<Prescription>>()
    val prescriptions: LiveData<List<Prescription>>
        get() = _prescriptions

    fun savePrescription(onComplete: (Boolean) -> Unit) {
        val id = firestore.collection("Prescription").document().id
        prescription.prescriptionId = id
        firestore.collection("Prescription")
            .document(id)
            .set(prescription)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }

    fun uploadImageSavePrescription(prescription: Prescription, onComplete: (Boolean) -> Unit) {
        imageUri?.let { uri ->
            val imageFileName = UUID.randomUUID().toString()
            imageRef = storageRef.child("images/$imageFileName.jpg")
            val uploadTask = imageRef.putFile(uri)
            uploadTask.addOnSuccessListener { uploadTaskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    prescription.image = downloadUri.toString()
                    savePrescription { success ->
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
            savePrescription { success ->
                if (success) {
                    onComplete(true)
                } else {
                    imageUploadResult.value = false
                }
            }
        }
    }


    fun allOrders(ownerId: String) {
        firestore.collection("Prescription")
            .whereEqualTo("userId", ownerId)
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
                // Handle failure
            }
    }
}
