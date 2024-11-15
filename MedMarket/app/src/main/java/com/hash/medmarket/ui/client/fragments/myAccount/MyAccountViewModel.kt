package com.hash.medmarket.ui.client.fragments.myAccount

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
import com.hash.medmarket.database.model.Store
import com.hash.medmarket.database.model.Users
import java.util.UUID

class MyAccountViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    fun updateUser(user: Users, onComplete: (Boolean) -> Unit) {
        firestore.collection("Users")
            .document(user.userId!!)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.e("FirestoreUpdate", "store: ${user.userId}")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreUpdate", "Error updating store: $e")
                onComplete(false)
            }
    }




}
