package com.hash.medmarket.ui.client.fragments.myOrders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.database.model.Order
import com.hash.medmarket.databinding.MyOrderListItemBinding


class OrdersAdapter(
    private val orders: List<Order>,
    private val onClick: (Order, Int, String) -> Unit
) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MyOrderListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class ViewHolder(private val binding: MyOrderListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {

            binding.textOrderId.text = "Order Id : ${order.orderId}"
            binding.textOrderDate.text = "Order Date : ${order.timestamp}"
            binding.textStatus.text = "Order Status : ${order.status}"

            // For showing price and medicine's count

            var totalPrice = 0
            order.medicines.forEach {
                totalPrice+=it.price!!.toInt()
            }

            binding.textOrderPrice.text = "$totalPrice PKR | ${order.medicines.size} medicines"

            var location = ""
            FirebaseFirestore.getInstance().collection("Users")
                .document(order.pharmacistId.toString())
                .get().addOnSuccessListener {
                    location = it.get("location").toString()
                }

            binding.root.setOnClickListener {
                onClick(order, totalPrice, location)
            }

        }
    }
}
