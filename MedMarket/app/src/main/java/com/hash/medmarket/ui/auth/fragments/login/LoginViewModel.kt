package com.hash.medmarket.ui.auth.fragments.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.hash.medmarket.database.model.Users
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()


    fun loginUser(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    result.value = user != null
                } else {
                    result.value = false
                }
            }
        return result
    }

    suspend fun getUserData(): Users? {
        val userId = firebaseAuth.currentUser?.uid
        return if (userId != null) {
            Log.d("UserData", "Getting user data...")
            val userRef = firestore.collection("Users").document(userId)
            userRef.get().await().toObject(Users::class.java)
        } else {
            Log.d("UserData", "$userId")
            null
        }
    }


}