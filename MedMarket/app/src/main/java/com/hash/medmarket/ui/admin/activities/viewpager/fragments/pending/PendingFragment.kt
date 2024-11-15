package com.hash.medmarket.ui.admin.activities.viewpager.fragments.pending

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.FragmentPendingBinding
import com.hash.medmarket.notifications.NotificationSender
import com.hash.medmarket.ui.admin.activities.FullScreenImageActivity
import com.hash.medmarket.ui.admin.activities.viewpager.fragments.pending.adapter.PendingRequestAdapter
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.utils.ToastUtility

class PendingFragment : Fragment() {

    private lateinit var binding: FragmentPendingBinding
    private lateinit var viewModel: PendingViewModel
    private lateinit var adapter: PendingRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPendingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[PendingViewModel::class.java]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePendingRequests()

    }

    val waitingDialog by lazy { WaitingDialog(requireContext()) }
    override fun onDestroyView() {
        waitingDialog.dismiss()
        super.onDestroyView()
    }


    private fun observePendingRequests() {
        waitingDialog.show("Getting Requests")
        viewModel.allPendingRequests()
        viewModel.users.observe(viewLifecycleOwner, Observer { usersList ->
            waitingDialog.dismiss()
            if (usersList.isNotEmpty()) {
                binding.emptyData.visibility = View.GONE
                adapter = PendingRequestAdapter(
                    usersList.toMutableList(),
                    ::onApproveClick,
                    ::onRejectClick,
                    ::onImageClick
                )
                binding.recyclerPendingRequests.adapter = adapter
            } else {
                adapter = PendingRequestAdapter(usersList.toMutableList(), ::onApproveClick, ::onRejectClick, ::onImageClick )
                binding.recyclerPendingRequests.adapter = adapter
                binding.emptyData.visibility = View.VISIBLE
            }
        })
    }



    private fun onApproveClick(users: Users) {
        waitingDialog.show("Confirming pharmacist")
        viewModel.updateOrderStatus(users) { isSuccess ->
            waitingDialog.dismiss()
            if (isSuccess) {
                ToastUtility.successToast(requireContext(), "Approved.")
                NotificationSender.sendNotification(
                    "Pharmacist Approved",
                    "Your account has been approved by the admin",
                    requireContext(), users.token.toString()
                )
                observePendingRequests()
            } else {
                ToastUtility.errorToast(
                    requireContext(),
                    "Failed to approve."
                )
            }
        }

    }
    private fun onRejectClick(users: Users) {
        waitingDialog.show("Processing Request")
        viewModel.rejectRequest(users) { isSuccess ->
            waitingDialog.dismiss()
            if (isSuccess) {
                ToastUtility.successToast(requireContext(), "Rejected.")
                NotificationSender.sendNotification(
                    "Pharmacist Rejected",
                    "Your account has been rejected by the admin",
                    requireContext(), users.token.toString()
                )
                observePendingRequests()
            } else {
                ToastUtility.errorToast(
                    requireContext(),
                    "Failed to reject."
                )
            }
        }

    }
    private fun onImageClick(imageUrl: String) {
        val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
        intent.putExtra("image_url", imageUrl)
        startActivity(intent)
    }



}
