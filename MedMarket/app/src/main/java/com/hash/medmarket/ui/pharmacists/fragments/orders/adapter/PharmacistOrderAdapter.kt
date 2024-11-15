package com.hash.medmarket.ui.pharmacists.fragments.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Order
import com.hash.medmarket.databinding.ItemOrderBinding


class PharmacistOrderAdapter(
    private val orders: List<Order>,
    val onConfirmClick : (Order) -> Unit,
    val onRejectClick: (Order) -> Unit,
    val onClick: (Order, Int , String) -> Unit
) :
    RecyclerView.Adapter<PharmacistOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.chipConfirm.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val store = orders[position]
                    onConfirmClick.invoke(store)
                }
            }
            binding.chipReject.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val store = orders[position]
                    onRejectClick.invoke(store)
                }
            }
        }

        fun bind(order: Order) {

            binding.textOrderId.text = "Order Id : ${order.orderId}"
            binding.textOrderDate.text = "Order Time : ${order.timestamp}"
            binding.textStatus.text = "Order Status : ${order.status}"

            // For showing price and medicine's count

            var totalPrice = 0
            order.medicines.forEach {
                totalPrice += it.price!!.toInt()
            }

            binding.textOrderPrice.text = "$totalPrice PKR | ${order.medicines.size} medicines"

            var location=""
            FirebaseFirestore.getInstance().collection("Users")
                .document(order.userId.toString())
                .get().addOnSuccessListener {
                    location = it.get("location").toString()
                }

            binding.root.setOnClickListener {
                onClick(order, totalPrice, location)
            }

            when (order.status) {
                "accepted" -> {
                    binding.chipConfirm.visibility = View.INVISIBLE
                    binding.chipReject.visibility = View.INVISIBLE
                    binding.chipStatus.visibility = View.VISIBLE
                }
                "rejected" -> {
                    binding.chipConfirm.visibility = View.INVISIBLE
                    binding.chipReject.visibility = View.INVISIBLE
                    binding.chipStatus.visibility = View.VISIBLE
                    binding.chipStatus.text = "Rejected"
                    binding.chipStatus.setChipBackgroundColorResource(R.color.red)
                }
                else -> {
                    binding.chipConfirm.visibility = View.VISIBLE
                    binding.chipReject.visibility = View.VISIBLE
                    binding.chipStatus.visibility = View.INVISIBLE
                }
            }

        }
    }
}
