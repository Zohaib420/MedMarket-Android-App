package com.hash.medmarket.ui.auth.fragments.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.database.model.Users

class ClientSignupViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    var user = Users()

    fun signUpClient(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    user.userId = userId
                    user.userType = "client"
                    addUserToFireStore(user) {
                        result.value = true
                    }
                } else {
                    result.value = false
                }
            }
        return result
    }

    private fun addUserToFireStore(user: Users, onComplete: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        val userRef = db.collection("Users").document(userId!!)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                userRef.set(user)
                    .addOnSuccessListener {
                         onComplete()
                    }
                    .addOnFailureListener { e -> onComplete() }
            } else {
                onComplete()
            }
        }
    }


//    private fun addUserToFirestore(user: Users, onComplete: () -> Unit) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("Users")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                onComplete()
//            }
//            .addOnFailureListener { e ->
//                onComplete()
//            }
//    }


}