package com.hash.medmarket.ui.pharmacists.fragments.medicine.addNew

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.database.model.Store
import java.util.UUID

class AddNewMedicineViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    var medicine = Medicine()

    private val storageRef = Firebase.storage.reference
    private lateinit var imageRef: StorageReference
    private val imageUploadResult = MutableLiveData<Boolean>()


    var imageUri: Uri? = null


    fun saveMedicine(medicineId: String?, onComplete: (Boolean) -> Unit) {
        if (medicineId.isNullOrEmpty()) {
            // If medicineId is null or empty, create a new document
            val id = firestore.collection("Medicine").document().id
            medicine.medicineId = id
            firestore.collection("Medicine")
                .document(id)
                .set(medicine)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            // If medicineId is provided, update the existing document
            firestore.collection("Medicine")
                .document(medicineId)
                .set(medicine)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }


    fun sameMedicineImageAndData(onComplete: (Boolean) -> Unit) {
        imageUri?.let { uri ->
            val imageFileName = UUID.randomUUID().toString()
            imageRef = storageRef.child("images/$imageFileName.jpg")
            val uploadTask = imageRef.putFile(uri)
            uploadTask.addOnSuccessListener { uploadTaskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    medicine.image = downloadUri.toString()
                    saveMedicine(medicine.medicineId){ success ->
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
            saveMedicine(medicine.medicineId) { success ->
                if (success) {
                    onComplete(true)
                } else {
                    imageUploadResult.value = false
                }
            }
        }
    }

}




