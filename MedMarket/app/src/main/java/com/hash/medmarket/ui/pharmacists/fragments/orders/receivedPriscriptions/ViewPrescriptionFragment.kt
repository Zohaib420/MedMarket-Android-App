package com.hash.medmarket.ui.pharmacists.fragments.orders.receivedPriscriptions

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hash.medmarket.database.model.Order
import com.hash.medmarket.database.model.Prescription
import com.hash.medmarket.databinding.FragmentOrdersBinding
import com.hash.medmarket.databinding.FragmentViewPrescriptionBinding
import com.hash.medmarket.ui.admin.activities.FullScreenImageActivity
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.pharmacists.fragments.orders.PharmacistOrderViewModel
import com.hash.medmarket.ui.pharmacists.fragments.orders.adapter.PharmacistOrderAdapter
import com.hash.medmarket.ui.pharmacists.fragments.orders.receivedPriscriptions.adapter.AllPrescriptionAdapter
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.text.FieldPosition

class ViewPrescriptionFragment : Fragment() {

    private lateinit var binding: FragmentViewPrescriptionBinding

    private lateinit var adapter: AllPrescriptionAdapter

    private lateinit var viewModel: ViewPrescriptionViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPrescriptionBinding.inflate(inflater, container, false)
        viewModel= ViewPrescriptionViewModel()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        observePrescriptions()

    }

    val waitingDialog by lazy { WaitingDialog(requireContext()) }
    override fun onDestroyView() {
        waitingDialog.dismiss()
        super.onDestroyView()
    }


    private fun observePrescriptions() {
        val userId = UserAuthManager.getUserData(requireContext())?.userId
        waitingDialog.show("Getting Prescriptions")
        if (userId!!.isNotEmpty()) {
            viewModel.allOrders(userId)
            viewModel.prescriptions.observe(viewLifecycleOwner, Observer { order ->
                waitingDialog.dismiss()
                if (order.isNotEmpty()) {
                    binding.emptyData.visibility = View.GONE
                    adapter = AllPrescriptionAdapter(requireContext(),order, ::onConfirmClick, ::onRejectClick,::onImageClick)
                    binding.rvPrescriptions.adapter = adapter
                } else {
                    binding.emptyData.visibility = View.VISIBLE
                    adapter = AllPrescriptionAdapter(requireContext(),order, ::onConfirmClick, ::onRejectClick,::onImageClick)
                    binding.rvPrescriptions.adapter = adapter
                    ToastUtility.errorToast(requireContext(), "No Prescription Found.")
                }
            })
        } else {
            waitingDialog.dismiss()
            binding.emptyData.visibility = View.VISIBLE
            ToastUtility.errorToast(requireContext(), "No Prescription Found.")
        }
    }



    private fun initClickListeners() {

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onImageClick(imageUrl: String) {
        val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
        intent.putExtra("image_url", imageUrl)
        startActivity(intent)
    }

    private fun onConfirmClick(order: Prescription, position: Int) {
        waitingDialog.show("Confirming prescription")
        viewModel.updateOrderStatusToAvailable(order) { isSuccess ->
            waitingDialog.dismiss()
            if (isSuccess) {
                observePrescriptions()
                ToastUtility.successToast(requireContext(), "Order Confirmed successfully.")
            } else {
                ToastUtility.errorToast(
                    requireContext(),
                    "Failed to confirm order. Please try again."
                )
            }
        }

    }
    private fun onRejectClick(order: Prescription, position: Int) {
        waitingDialog.show("Rejecting prescription")
        viewModel.updateOrderStatusToReject(order) { isSuccess ->
            waitingDialog.dismiss()
            if (isSuccess) {
                observePrescriptions()
                ToastUtility.successToast(requireContext(), "Prescription Rejected successfully.")
            } else {
                ToastUtility.errorToast(
                    requireContext(),
                    "Failed to confirm order. Please try again."
                )
            }
        }

    }



}
