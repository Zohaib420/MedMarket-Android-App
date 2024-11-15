package com.hash.medmarket.utils

import android.content.Context
import com.google.gson.Gson
import com.hash.medmarket.database.model.Medicine

object CartManager {
    private const val CART_PREFS_NAME = "CartPrefs"

    fun saveCartData(context: Context, cart: Cart) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("cartData", Gson().toJson(cart))
        editor.apply()
    }

    fun getCartData(context: Context): Cart {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS_NAME, Context.MODE_PRIVATE)
        val cartJson = sharedPreferences.getString("cartData", "")
        return Gson().fromJson(cartJson, Cart::class.java) ?: Cart.getInstance()
    }

    fun clearCartData(context: Context) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("cartData")
        editor.apply()
//        Cart.getInstance().clearMedicines()
    }



}
