package com.hash.medmarket.ui.client.fragments.home.prescription

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Prescription
import com.hash.medmarket.databinding.ItemPrescriptionBinding
import com.hash.medmarket.utils.UserAuthManager


class PrescAdapter(
    val context: Context,
    private val orders: List<Prescription>,
    val onConfirmClick: (Prescription, Int) -> Unit,
    val onRejectClick: (Prescription, Int) -> Unit,
    val onImageClick: (String) -> Unit

) :
    RecyclerView.Adapter<PrescAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPrescriptionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class ViewHolder(private val binding: ItemPrescriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivPrescription.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val imageUrl = orders[position].image
                    onImageClick.invoke(imageUrl!!)
                }
            }

        }


        fun bind(order: Prescription) {

            val clientName = order.userName ?: "Unknown"

            if (UserAuthManager.getUserData(context)!!.userType == "client") {
                binding.textStoreName.text = "Date : ${order.timestamp}"
            } else {
                binding.textStoreName.text = "Client Name: $clientName"
            }


            binding.textPhone.text = "Phone: ${order.phone}"
            binding.textAddress.visibility = View.INVISIBLE

            binding.chipReject.setOnClickListener {
                onRejectClick(order, adapterPosition)
            }
            binding.chipConfirm.setOnClickListener {
                onConfirmClick(order, adapterPosition)
            }

            when (order.status) {
                "available" -> {
                    binding.chipConfirm.visibility = View.INVISIBLE
                    binding.chipReject.visibility = View.INVISIBLE
                    binding.chipStatus.visibility = View.VISIBLE
                }

                "rejected" -> {
                    binding.chipConfirm.visibility = View.INVISIBLE
                    binding.chipReject.visibility = View.INVISIBLE
                    binding.chipStatus.visibility = View.VISIBLE
                    binding.chipStatus.text = "Not Available"
                    binding.chipStatus.setChipBackgroundColorResource(R.color.red)
                }

                "pending" -> {
                    binding.chipConfirm.visibility = View.INVISIBLE
                    binding.chipReject.visibility = View.INVISIBLE
                    binding.chipStatus.visibility = View.VISIBLE
                    binding.chipStatus.text = "Pending"
                    binding.chipStatus.setChipBackgroundColorResource(R.color.red)
                }

                else -> {
                    binding.chipConfirm.visibility = View.VISIBLE
                    binding.chipReject.visibility = View.VISIBLE
                    binding.chipStatus.visibility = View.INVISIBLE
                }
            }

            binding.ivPrescription.load(order.image) {
                placeholder(R.drawable.store_image)
                error(R.drawable.store_image)
            }
        }
    }
}
