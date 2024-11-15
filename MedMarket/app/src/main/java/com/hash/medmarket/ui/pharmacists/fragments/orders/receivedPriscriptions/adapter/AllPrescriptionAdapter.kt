package com.hash.medmarket.ui.pharmacists.fragments.orders.receivedPriscriptions.adapter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Order
import com.hash.medmarket.database.model.Prescription
import com.hash.medmarket.databinding.ItemOrderBinding
import com.hash.medmarket.databinding.ItemPrescriptionBinding
import java.util.Locale


class AllPrescriptionAdapter(
    val context: Context,
    private val orders: List<Prescription>,
    val onConfirmClick : (Prescription, Int) -> Unit,
    val onRejectClick : (Prescription, Int) -> Unit,
    val onImageClick : (String)->Unit

) :
    RecyclerView.Adapter<AllPrescriptionAdapter.ViewHolder>() {

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

            binding.textStoreName.text = order.userName
            binding.textPhone.text = order.phone
            try {
                val parts = order.location!!.split(",")
                val latitude = parts[0]
                val longitude = parts[1]

                binding.textAddress.text =
                    getAddress(latitude.toDouble(), longitude.toDouble())
            }
            catch (e:Exception){}


            binding.chipReject.setOnClickListener{
                onRejectClick(order, adapterPosition)
            }
            binding.chipConfirm.setOnClickListener{
                onConfirmClick(order,adapterPosition)
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
    private fun getAddress(latitude: Double, longitude: Double): String {
        val addresses: List<Address>
        val geocoder = Geocoder(context, Locale.getDefault())
        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        )!!
        val address: String = addresses[0].getAddressLine(0)

        return address
    }

}
