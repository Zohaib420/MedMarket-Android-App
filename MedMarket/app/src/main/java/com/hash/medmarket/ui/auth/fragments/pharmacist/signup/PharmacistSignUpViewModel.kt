package com.hash.medmarket.ui.auth.fragments.pharmacist.signup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.hash.medmarket.database.model.Users
import java.util.UUID

class PharmacistSignUpViewModel() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storageRef = Firebase.storage.reference
    private lateinit var imageRef: StorageReference
    private val imageUploadResult = MutableLiveData<Boolean>()


    var imageUri: Uri? = null
    var imageUriLicesne: Uri? = null

    private val firebaseAuth = FirebaseAuth.getInstance()

    var user = Users()

    fun signUpPharmacist(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    user.userId = userId
                    user.userType = "pharmacist"
                    uploadImageAndAddUserToFirestore(user) {
                        result.value = true
                    }
                } else {
                    result.value = false
                }
            }
        return result
    }

    private fun uploadImageAndAddUserToFirestore(user: Users, onComplete: () -> Unit) {
        var profileImageUploaded = false
        var licenseImageUploaded = false

        imageUri?.let { uri ->
            val profileImageFileName = UUID.randomUUID().toString()
            val profileImageRef = storageRef.child("images/$profileImageFileName.jpg")
            val profileUploadTask = profileImageRef.putFile(uri)
            profileUploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                profileImageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    user.image = downloadUri.toString()
                    profileImageUploaded = true
                    if (licenseImageUploaded) {
                        addUserToFireStore(user) { onComplete() }
                    }
                } else {
                    // Handle failures
                    imageUploadResult.value = false
                }
            }
        } ?: run {
            // No profile image selected
            profileImageUploaded = true
        }

        imageUriLicesne?.let { uri ->
            val licenseImageFileName = UUID.randomUUID().toString()
            val licenseImageRef = storageRef.child("images/$licenseImageFileName.jpg")
            val licenseUploadTask = licenseImageRef.putFile(uri)
            licenseUploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                licenseImageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    user.licenseImage = downloadUri.toString()
                    licenseImageUploaded = true
                    if (profileImageUploaded) {
                        addUserToFireStore(user) { onComplete() }
                    }
                } else {
                    // Handle failures
                    imageUploadResult.value = false
                }
            }
        } ?: run {
            // No license image selected
            licenseImageUploaded = true
        }
    }


    private fun addUserToFireStore(user: Users, onComplete: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        val userRef = db.collection("Users").document(userId!!)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                userRef.set(user)
                    .addOnSuccessListener {

                        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnSuccessListener {
                            onComplete()
                        }

                    }
                    .addOnFailureListener { e -> onComplete() }
            } else {
                onComplete()
            }
        }
    }


}