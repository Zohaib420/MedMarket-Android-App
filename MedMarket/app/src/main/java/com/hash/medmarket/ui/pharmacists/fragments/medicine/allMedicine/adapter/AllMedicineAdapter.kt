package com.hash.medmarket.ui.pharmacists.fragments.medicine.allMedicine.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.database.model.Store
import com.hash.medmarket.databinding.ItemAllMedicineBinding
import com.hash.medmarket.databinding.ItemStoreBinding

class AllMedicineAdapter(
    private val medicine: List<Medicine>,
    val onOptionClick : (Medicine) -> Unit
) :
    RecyclerView.Adapter<AllMedicineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAllMedicineBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = medicine[position]
        holder.bind(store)
    }

    override fun getItemCount(): Int = medicine.size

    inner class ViewHolder(private val binding: ItemAllMedicineBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.btnSelectOption.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val store = medicine[position]
                    onOptionClick.invoke(store)
                }
            }
        }

        fun bind(medicine: Medicine) {
            binding.tvName.text = medicine.name
            binding.tvDescription.text = medicine.description
            binding.tvPrice.text = medicine.price
            binding.ivMedicine.load(medicine.image)
        }
    }
}
