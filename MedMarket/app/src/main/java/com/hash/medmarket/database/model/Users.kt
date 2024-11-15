package com.hash.medmarket.database.model

import java.io.Serializable

data class Users(
    var userId: String? = null,
    var userType: String? = null, // pharmacist or client
    var email: String? = null,
    var name: String? = null,
    var timeStamp: String?=null,
    var status: String? = null,  //Only for pharmacist
    var licenseImage: String? = null, // pharmacist license image
    var image: String? = null, // pharmacist profile image
    var token: String? = null, // FCM
    var password: String? = null,
    var location : String?=null,
    var phone : String?=null
):Serializable
