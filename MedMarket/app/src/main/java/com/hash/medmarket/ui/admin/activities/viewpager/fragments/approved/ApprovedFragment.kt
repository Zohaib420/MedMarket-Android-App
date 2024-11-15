package com.hash.medmarket.ui.admin.activities.viewpager.fragments.approved

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.FragmentApprovedBinding
import com.hash.medmarket.databinding.FragmentPendingBinding
import com.hash.medmarket.ui.admin.activities.FullScreenImageActivity
import com.hash.medmarket.ui.admin.activities.viewpager.fragments.pending.PendingViewModel
import com.hash.medmarket.ui.admin.activities.viewpager.fragments.pending.adapter.PendingRequestAdapter
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.utils.ToastUtility

class ApprovedFragment : Fragment() {

    private lateinit var binding: FragmentApprovedBinding
    private lateinit var viewModel: ApprovedViewModel
    private lateinit var adapter: PendingRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApprovedBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ApprovedViewModel::class.java]

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

    override fun onResume() {
        super.onResume()
        observePendingRequests()
    }


    fun observePendingRequests() {
        waitingDialog.show("Getting Requests")
        viewModel.allApprovedRequests()
        viewModel.users.observe(viewLifecycleOwner, Observer { medicine ->
            waitingDialog.dismiss()
            if (medicine.isNotEmpty()) {
                binding.emptyData.visibility = View.GONE
                adapter = PendingRequestAdapter(medicine,::onApproveClick, ::onRejectClick, ::onImageClick )
                binding.recyclerApprovedRequests.adapter = adapter
            } else {
                adapter = PendingRequestAdapter(medicine,::onApproveClick, ::onRejectClick, ::onImageClick )
                binding.recyclerApprovedRequests.adapter = adapter
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
