package com.hash.medmarket.notifications

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hash.medmarket.R
import org.json.JSONObject

object NotificationSender {

    fun sendNotification(title: String, message: String, context: Context, token: String) {

        val to = JSONObject()
        val data = JSONObject()
        try {
            data.put("title", title)
            data.put("message", message)
            data.put("hisID", "")
            data.put("hisImage", "")

            to.put("to", token)
            to.put("data", data)

            val request: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                to,
                { _ -> },
                { _ -> }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val map: MutableMap<String, String> = HashMap()
                    map["Authorization"] = "key=" + context.getString(R.string.fcm_key)
                    map["Content-Type"] = "application/json"
                    return map
                }
            }

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            request.setRetryPolicy(
                DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            requestQueue.add(request)

        } catch (e: Exception) {

        }

    }

}