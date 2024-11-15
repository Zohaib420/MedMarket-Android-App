package com.hash.medmarket.database.model

import java.io.Serializable

data class Medicine(
    var medicineId: String? = null,
    var storeId: String? = null, // reference to Stores Collection
    var ownerId: String? = null, // reference to Stores Collection
    var name: String? = null,
    var description: String? = null,
    var quantity: String? = null,
    var category: String? = null,
    var timeStamp: String?=null,
    var price: String? = null,
    var image: String? = null // medicine image
) : Serializable

