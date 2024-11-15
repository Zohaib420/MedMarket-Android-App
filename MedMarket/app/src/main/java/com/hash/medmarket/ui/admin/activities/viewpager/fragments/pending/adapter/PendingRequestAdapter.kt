package com.hash.medmarket.ui.admin.activities.viewpager.fragments.pending.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.ItemUsersBinding

class PendingRequestAdapter(
    private val users: List<Users>,
    val onApproveClick : (Users) -> Unit,
    val onRejectClick : (Users) -> Unit,
    val onImageClick : (String)->Unit
) :
    RecyclerView.Adapter<PendingRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUsersBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = users[position]
        holder.bind(store)

    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(private val binding: ItemUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.btnApprove.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val store = users[position]
                    onApproveClick.invoke(store)
                }
            }
            binding.btnReject.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val store = users[position]
                    onRejectClick.invoke(store)
                }
            }
            binding.ivLicenseIamge.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val imageUrl = users[position].licenseImage
                    onImageClick.invoke(imageUrl!!)
                }
            }
        }

        fun bind(user: Users) {
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.ivLicenseIamge.load(user.licenseImage){
                placeholder(R.drawable.ic_house_gray)
                error(R.drawable.ic_house_gray)
            }


            when (user.status) {
                "approved" -> {
                    binding.btnApproved.visibility = View.GONE
                    binding.btnApprove.visibility = View.GONE
                    binding.btnReject.visibility = View.GONE

                }

                "pending" -> {
                    binding.btnApproved.visibility = View.GONE
                    binding.btnApprove.visibility = View.VISIBLE
                    binding.btnReject.visibility = View.VISIBLE

                }
                "rejected" -> {
                    binding.btnApproved.visibility = View.VISIBLE
                    binding.btnApproved.text="Rejected , License not clear"
                    binding.btnApproved.setBackgroundColor(binding.root.context.resources.getColor(R.color.red))
                    binding.btnApprove.visibility = View.GONE
                    binding.btnReject.visibility = View.GONE
                }
            }
        }
    }
}
