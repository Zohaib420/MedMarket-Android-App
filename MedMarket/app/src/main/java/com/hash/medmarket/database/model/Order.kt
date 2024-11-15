package com.hash.medmarket.database.model

import java.sql.Timestamp

data class Order(
    var orderId: String? = null,
    var userId: String? = null, // reference to Users Collection
    var pharmacistId: String? = null,
    var medicines: List<Medicine> = emptyList(),
    var timestamp: String?=null,
    var name: String?=null,
    var phone: String?=null,
    var status: String? = null, // pending or delivered
)

