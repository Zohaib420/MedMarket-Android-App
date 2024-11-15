package com.hash.medmarket.ui.admin.activities.viewpager.fragments.rejected

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.FragmentRejectedBinding
import com.hash.medmarket.ui.admin.activities.FullScreenImageActivity
import com.hash.medmarket.ui.admin.activities.viewpager.fragments.pending.PendingViewModel
import com.hash.medmarket.ui.admin.activities.viewpager.fragments.pending.adapter.PendingRequestAdapter
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.utils.ToastUtility

class RejectedFragment : Fragment() {

    private lateinit var binding: FragmentRejectedBinding
    private lateinit var viewModel: RejectedViewModel
    private lateinit var adapter: PendingRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRejectedBinding.inflate(inflater, container, false)
        viewModel= RejectedViewModel()
        observePendingRequests()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    val waitingDialog by lazy { WaitingDialog(requireContext()) }
    override fun onDestroyView() {
        waitingDialog.dismiss()
        super.onDestroyView()
    }


    private fun observePendingRequests() {
        waitingDialog.show("Getting Requests")
        viewModel.allRejectRequests()
        viewModel.users.observe(viewLifecycleOwner, Observer { medicine ->
            waitingDialog.dismiss()
            if (medicine.isNotEmpty()) {
                binding.emptyData.visibility = View.GONE
                adapter = PendingRequestAdapter(medicine,::onApproveClick,::onRejectClick , ::onImageClick)
                binding.recyclerPendingRequests.adapter = adapter
            } else {
                binding.emptyData.visibility = View.VISIBLE
            }
        })

    }


    private fun onApproveClick(users: Users) {

    }
    private fun onRejectClick(users: Users) {

    }

    private fun onImageClick(imageUrl: String) {
        val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
        intent.putExtra("image_url", imageUrl)
        startActivity(intent)
    }



}
