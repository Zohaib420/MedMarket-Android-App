package com.hash.medmarket.ui.client.fragments.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.databinding.ItemMedicineBinding

class MedicineAdapter(
    private val medicine: List<Medicine>,
    private val selectedMedicineIds: MutableSet<String>, // Change to MutableSet

    val onMedicineClick: (Medicine, String, Int) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMedicineBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, selectedMedicineIds) // Pass selectedMedicineIds to ViewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = medicine[position]
        holder.bind(store, position)
    }

    override fun getItemCount(): Int = medicine.size

    fun setSelectedMedicine(medicineId: String?) {
        if (medicineId != null) {
            selectedMedicineIds.add(medicineId)
            notifyDataSetChanged() // Notify adapter of changes
        }
    }

    inner class ViewHolder(
        private val binding: ItemMedicineBinding,
        private val selectedMedicineIds: MutableSet<String> // Receive selectedMedicineIds
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medicine: Medicine, position: Int) {

            binding.tvMedicineName.text = medicine.name
            binding.tvDescription.text = medicine.description
            binding.tvPrice.text = "Rs ${medicine.price}"
            binding.ivMedicine.load(medicine.image)

            binding.checkbox.isChecked = isSelected(medicine.medicineId!!)

            binding.checkbox.setOnClickListener {
                onMedicineClick.invoke(medicine, medicine.medicineId ?: "", position)
                updateSelectedMedicineIds(medicine.medicineId)
            }
        }

        private fun isSelected(medicineId: String): Boolean {
            return medicineId in selectedMedicineIds
        }

        private fun updateSelectedMedicineIds(medicineId: String?) {
            if (medicineId != null) {
                if (isSelected(medicineId)) {
                    selectedMedicineIds.remove(medicineId)
                } else {
                    selectedMedicineIds.add(medicineId)
                }
            }
        }
    }
}
