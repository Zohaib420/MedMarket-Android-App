package com.hash.medmarket.utils

import com.hash.medmarket.database.model.Medicine
class Cart private constructor() {
    companion object {
        fun getInstance(): Cart {
            return Holder.INSTANCE
        }
    }

    private object Holder {
        val INSTANCE = Cart()
    }

    var medicines: MutableList<Medicine> = mutableListOf()

    fun addMedicine(medicine: Medicine) {
        medicines.add(medicine)
    }

    fun removeMedicine(medicine: Medicine) {
        medicines.remove(medicine)
    }
    fun clearMedicines() {
        medicines.clear()
    }

}
