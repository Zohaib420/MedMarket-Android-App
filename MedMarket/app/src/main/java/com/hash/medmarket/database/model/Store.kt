package com.hash.medmarket.database.model

import java.io.Serializable

data class Store(
    var storeId: String? = null,
    var ownerId: String? = null, // reference to Users Collection
    var name: String? = null,
    var email: String? = null,
    var image: String? = null,
    var timestamp: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null
) : Serializable

