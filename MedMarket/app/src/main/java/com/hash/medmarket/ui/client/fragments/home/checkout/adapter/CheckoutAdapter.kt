package com.hash.medmarket.ui.client.fragments.home.checkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.databinding.ItemCheckoutBinding

class CheckoutAdapter(
    private val medicines: MutableList<Medicine>,
    private val onCheckedClick: (Medicine, Boolean, String, Boolean) -> Unit,
    private val totalPriceListener: TotalPriceUpdateListener,
    private val selectedAdapter: SelectedItemAdapter
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    private val selectedMedicines: MutableMap<Medicine, Int> = mutableMapOf()
    private val selectedQuantities: MutableMap<Medicine, Int> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCheckoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, selectedAdapter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.bind(medicine)
    }

    override fun getItemCount(): Int = medicines.size


    inner class ViewHolder(
        private val binding: ItemCheckoutBinding,
        private val selectedAdapter: SelectedItemAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentMedicine: Medicine? = null

        init {
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val medicine = medicines[position]
                    if (isChecked) {
                        selectedMedicines[medicine] = 1
                    } else {
                        selectedMedicines.remove(medicine)
                    }
                    updateTotalPrice()
                    onCheckedClick.invoke(medicine, isChecked, binding.tvCountQuantity.text.toString(), isChecked)
                }
            }

            binding.tvCountQuantity.setOnClickListener {
                val quantity = binding.tvCountQuantity.text.toString().toInt()
                if (quantity == 0) {
                    showClearCartDialog(currentMedicine)
                }
            }

            binding.btnPlus.setOnClickListener {
                val quantity = binding.tvCountQuantity.text.toString().toInt()
                if (binding.checkbox.isChecked && quantity < currentMedicine?.quantity!!.toInt()) {
                    // Increment quantity
                    val updatedQuantity = quantity + 1
                    binding.tvCountQuantity.text = updatedQuantity.toString()
                    selectedQuantities[currentMedicine!!] = updatedQuantity // Update quantity in selectedQuantities
                    val updatedPrice = calculateTotalPrice(currentMedicine!!, updatedQuantity)
                    binding.tvPrice.text = "Rs $updatedPrice"
                    onCheckedClick.invoke(currentMedicine!!, true, updatedQuantity.toString(), true)
                    updateTotalPrice()
                }
            }

            binding.btnMinus.setOnClickListener {
                val quantity = binding.tvCountQuantity.text.toString().toInt()
                if (binding.checkbox.isChecked && quantity > 1) {
                    // Decrement quantity
                    val updatedQuantity = quantity - 1
                    binding.tvCountQuantity.text = updatedQuantity.toString()
                    selectedQuantities[currentMedicine!!] = updatedQuantity // Update quantity in selectedQuantities
                    val updatedPrice = calculateTotalPrice(currentMedicine!!, updatedQuantity)
                    binding.tvPrice.text = "Rs $updatedPrice"
                    onCheckedClick.invoke(currentMedicine!!, true, updatedQuantity.toString(), false)
                    updateTotalPrice()
                } else if (quantity == 1 && binding.checkbox.isChecked) {
                    showClearCartDialog(currentMedicine)
                }
            }
        }

        fun bind(medicine: Medicine) {
            currentMedicine = medicine
            binding.tvName.text = medicine.name
            binding.tvDescription.text = medicine.description
            binding.ivMedicine.load(medicine.image)
            binding.checkbox.isChecked = selectedMedicines.containsKey(medicine)

            // Set initial quantity and price
            val quantity = selectedQuantities[medicine] ?: 1
            binding.tvCountQuantity.text = quantity.toString()
            binding.tvPrice.text = "Rs ${medicine.price}"
        }

        private fun calculateTotalPrice(medicine: Medicine, quantity: Int): Int {
            return medicine.price!!.toInt() * quantity
        }

        private fun updateTotalPrice() {
            var totalPrice = 0
            selectedMedicines.forEach { (medicine, _) ->
                val quantity = selectedQuantities[medicine] ?: 1
                totalPrice += calculateTotalPrice(medicine, quantity)
            }
            totalPriceListener.onTotalPriceUpdate(totalPrice)
        }

        private fun showClearCartDialog(medicine: Medicine?) {
            val dialog = AlertDialog.Builder(itemView.context)
            dialog.setTitle("Clear Cart")
            dialog.setMessage("Do you want to clear this item from the cart?")
            dialog.setPositiveButton("Yes") { _, _ ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    medicine?.let {
                        selectedMedicines.remove(it)
                        selectedQuantities.remove(it)
                        medicines.removeAt(position)
                        selectedAdapter.removeMedicine(it)
                        notifyItemRemoved(position)
                        updateTotalPrice()
                    }
                }
            }
            dialog.setNegativeButton("No") { _, _ -> }
            dialog.show()
        }
    }

    fun clearItems() {
        medicines.clear()
        notifyDataSetChanged()
    }

    interface TotalPriceUpdateListener {
        fun onTotalPriceUpdate(totalPrice: Int)
    }
}
