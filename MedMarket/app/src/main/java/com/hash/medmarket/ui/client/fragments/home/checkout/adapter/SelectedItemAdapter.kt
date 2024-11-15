package com.hash.medmarket.ui.client.fragments.home.checkout.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.databinding.ItemSelectedBinding

class SelectedItemAdapter(private val selectedMedicines: MutableList<Medicine>) :
    RecyclerView.Adapter<SelectedItemAdapter.ViewHolder>() {


    private val selectedQuantities: MutableMap<Medicine, Int> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectedBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = selectedMedicines[position]
        holder.bind(medicine)
    }

    override fun getItemCount(): Int = selectedMedicines.size

    inner class ViewHolder(private val binding: ItemSelectedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medicine: Medicine) {
            binding.tvName.text = medicine.name
            binding.tvPrice.text = "Rs ${calculateTotalPrice(medicine)}"

        }

    }
    private fun calculateTotalPrice(medicine: Medicine): Int {
        val price = medicine.price!!.toInt()
        val quantity = selectedQuantities[medicine] ?: 0
        return price * quantity
    }

    fun addMedicine(medicine: Medicine) {
        if (selectedMedicines.contains(medicine)) {
            val currentQuantity = selectedQuantities[medicine] ?: 0
            selectedQuantities[medicine] = currentQuantity + 1
        } else {
            selectedMedicines.add(medicine)
            selectedQuantities[medicine] = 1 // Start quantity from 1
        }
        notifyDataSetChanged()
    }

    fun removeMedicine(medicine: Medicine) {
        if ((selectedQuantities[medicine] ?: 0) > 1) {
            val currentQuantity = selectedQuantities[medicine] ?: 0
            selectedQuantities[medicine] = currentQuantity - 1
        } else {
            selectedMedicines.remove(medicine)
            selectedQuantities.remove(medicine)
        }
        notifyDataSetChanged()
    }



    fun getSelectedMedicines(): List<Medicine> {
        return selectedMedicines.toList()
    }

    fun getTotalPrice(): Int {
        var totalPrice = 0
        selectedMedicines.forEach { medicine ->
            totalPrice += calculateTotalPrice(medicine)
        }
        return totalPrice
    }

    fun updateMedicine(quantity: String, cart: Medicine, isAdd: Boolean) {
        val currentQuantity = selectedQuantities[cart] ?: 0
        val updatedQuantity = if (isAdd) currentQuantity + 1 else currentQuantity - 1

        if (updatedQuantity > 0) {
            selectedQuantities[cart] = updatedQuantity
        } else {
            // Remove the medicine if the quantity becomes zero or negative
            selectedMedicines.remove(cart)
            selectedQuantities.remove(cart)
        }

        notifyDataSetChanged()
    }


}

