package com.hash.medmarket.model.orders

data class OrdersModel(
    var id: Int? = null,
    var orderID: String? = null,
    var customerName: String? = null,
    var orderDate: String? = null,
    var totalCost: String? = null,
    var status: String? = null
)
