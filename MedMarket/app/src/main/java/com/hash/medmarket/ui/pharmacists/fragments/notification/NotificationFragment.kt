package com.hash.medmarket.ui.pharmacists.fragments.notification

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hash.medmarket.R
import com.hash.medmarket.databinding.FragmentNotificationBinding
import com.hash.medmarket.databinding.FragmentPharmcistHomeBinding
import com.hash.medmarket.databinding.FragmentProfileBinding
import com.hash.medmarket.databinding.FragmentStoreManagementBinding
import com.hash.medmarket.model.notification.NotificationModel
import com.hash.medmarket.ui.pharmacists.activities.PharmacistDrawerListener
import com.hash.medmarket.ui.pharmacists.fragments.notification.adapter.NotificationAdapter

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

    private lateinit var adapter: NotificationAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initNotificationAdapter()
    }

    private fun initNotificationAdapter() {
        val notificationList = listOf(
            NotificationModel(id = 1, title = "Notification 1", message = "This is the first notification", date = "2023-02-20"),
            NotificationModel(id = 2, title = "Notification 2", message = "This is the second notification", date = "2023-02-19"),
            NotificationModel(id = 3, title = "Notification 3", message = "This is the third notification", date = "2023-02-18")
        )
        binding.recyclerNotification.adapter = NotificationAdapter(notificationList)

    }

    private fun initClickListeners() {


        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }


    }


}
