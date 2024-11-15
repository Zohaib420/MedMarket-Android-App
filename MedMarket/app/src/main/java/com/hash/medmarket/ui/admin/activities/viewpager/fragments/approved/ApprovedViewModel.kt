package com.hash.medmarket.ui.admin.activities.viewpager.fragments.approved

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.hash.medmarket.database.model.Users

class ApprovedViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _users = MutableLiveData<List<Users>>()
    val users: LiveData<List<Users>>
        get() = _users


    fun allApprovedRequests() {
        firestore.collection("Users")
            .whereEqualTo("status", "approved")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w("Error", "Listen failed", e)
                    return@EventListener
                }

                val usersList = mutableListOf<Users>()
                for (document in snapshots!!) {
                    val users = document.toObject(Users::class.java)
                    usersList.add(users)
                }
                _users.value = usersList

            })
    }


}
