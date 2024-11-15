package com.hash.medmarket.ui.client.fragments.home.adapter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.ItemStoreBinding
import com.hash.medmarket.databinding.ItemStoreClientBinding
import java.util.Locale

class StoreAdapter(
    val context: Context,
    private val stores: List<Users>,
    val onStoreClick: (Users) -> Unit
) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoreClientBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores[position]
        holder.bind(store)
    }

    override fun getItemCount(): Int = stores.size

    inner class ViewHolder(private val binding: ItemStoreClientBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val store = stores[position]
                    onStoreClick.invoke(store)
                }
            }
        }

        fun bind(store: Users) {

            binding.textStoreName.text = store.name

            try {
                val parts = store.location!!.split(",")
                val latitude = parts[0]
                val longitude = parts[1]

                binding.textStoreAddress.text =
                    getAddress(latitude.toDouble(), longitude.toDouble())
            }
            catch (e:Exception){}

            binding.textPhoneNumber.text = store.phone
            binding.imageStore.load(store.image) {
                R.drawable.store_image
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
