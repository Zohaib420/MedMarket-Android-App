package com.hash.medmarket.database.model

data class Prescription(
    var prescriptionId: String? = null,
    var userId: String? = null,
    var storeId: String?=null,
    var location: String?=null,
    var phone: String?=null,
    var pharmacistId: String? = null,
    var userName: String? = null,
    var status: String? = null, // "pending", "approved", "rejected"
    var image: String? = null,
    var timestamp: String? = null
)

