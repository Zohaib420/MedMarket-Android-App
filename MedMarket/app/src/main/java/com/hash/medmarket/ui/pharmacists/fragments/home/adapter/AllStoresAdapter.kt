package com.hash.medmarket.ui.pharmacists.fragments.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Store
import com.hash.medmarket.databinding.ItemStoreBinding
import com.hash.medmarket.model.store.StoreModel

class AllStoresAdapter(
    private val stores: List<Store>,
    val onStoreClick : (Store) -> Unit,
    val onSelectMenu : (Store, Int) -> Unit
) :
    RecyclerView.Adapter<AllStoresAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoreBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores[position]
        holder.bind(store)
    }

    override fun getItemCount(): Int = stores.size

    inner class ViewHolder(private val binding: ItemStoreBinding) :
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

        fun bind(store: Store) {
            binding.textStoreName.text = store.name
            binding.textStoreAddress.text = store.address
            binding.textPhoneNumber.text = store.phoneNumber
            binding.imageStore.load(store.image){
                placeholder(R.drawable.store_image)
                error(R.drawable.store_image)
            }

            binding.btnSelectOption.setOnClickListener{
                onSelectMenu(store, adapterPosition)
            }
        }
    }
}
