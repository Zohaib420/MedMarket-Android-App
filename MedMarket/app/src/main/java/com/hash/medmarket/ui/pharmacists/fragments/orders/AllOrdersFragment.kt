package com.hash.medmarket.ui.pharmacists.fragments.orders

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Order
import com.hash.medmarket.databinding.FragmentOrdersBinding
import com.hash.medmarket.notifications.NotificationSender
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.pharmacists.fragments.orders.adapter.PharmacistOrderAdapter
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.util.Locale

class AllOrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding

    private lateinit var adapter: PharmacistOrderAdapter

    private lateinit var viewModel: PharmacistOrderViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        viewModel= PharmacistOrderViewModel()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        observeOrders()

    }

    val waitingDialog by lazy { WaitingDialog(requireContext()) }
    override fun onDestroyView() {
        waitingDialog.dismiss()
        super.onDestroyView()
    }


    private fun observeOrders() {
        val userId = UserAuthManager.getUserData(requireContext())?.userId
        waitingDialog.show("Getting Orders")
        if (userId!!.isNotEmpty()) {
            viewModel.allOrders(userId)
            viewModel.orders.observe(viewLifecycleOwner, Observer { order ->
                waitingDialog.dismiss()
                if (order.isNotEmpty()) {
                    binding.emptyData.visibility = View.GONE
                    adapter = PharmacistOrderAdapter(
                        order,
                        ::onConfirmClick,
                        ::onRejectClick,
                        onClick = { myOrder, total, location ->

                        val medicineList: StringBuilder = StringBuilder()

                        myOrder.medicines.forEach {
                            medicineList.append(it.name.toString() + ",")
                        }

                            val parts = location.split(",")
                            val latitude = parts[0]
                            val longitude = parts[1]
                            val address = getAddress(latitude.toDouble(), longitude.toDouble())


                        AlertDialog.Builder(requireContext())
                            .setTitle("Order Detail")
                            .setMessage("Name : ${myOrder.name}\nPhone Number : ${myOrder.phone}\nTotal Bill : $total PKR\nNo of medicines : ${myOrder.medicines.size}\nMedicines : $medicineList\nDate : ${myOrder.timestamp}\nAddress : $address")
                            .show()
                    })
                    binding.rvOrders.adapter = adapter
                } else {
                    binding.emptyData.visibility = View.VISIBLE
                }
            })
        } else {
            waitingDialog.dismiss()
            binding.emptyData.visibility = View.VISIBLE
        }
    }

    private fun onConfirmClick(order: Order) {
        waitingDialog.show("Confirming order")
        viewModel.updateOrderStatusToAccepted(order) { isSuccess ->
            waitingDialog.dismiss()
            if (isSuccess) {
                observeOrders()
                ToastUtility.successToast(requireContext(), "Order Confirmed successfully.")

                FirebaseFirestore.getInstance().collection("Users")
                    .document(order.userId.toString())
                    .get().addOnSuccessListener {
                        NotificationSender.sendNotification(
                            "Order Update",
                            "Your order has been confirmed.",
                            requireContext(),it.get("token").toString()
                        )
                    }


            } else {
                ToastUtility.errorToast(
                    requireContext(),
                    "Failed to confirm order. Please try again."
                )
            }
        }

    }

    private fun onRejectClick(order: Order) {
        waitingDialog.show("Rejecting order")
        viewModel.updateOrderStatusToRejected(order) { isSuccess ->
            waitingDialog.dismiss()
            if (isSuccess) {
                observeOrders()
                FirebaseFirestore.getInstance().collection("Users")
                    .document(order.userId.toString())
                    .get().addOnSuccessListener {
                        NotificationSender.sendNotification(
                            "Order Update",
                            "Your order has been rejected.",
                            requireContext(),it.get("token").toString()
                        )
                    }
                ToastUtility.successToast(requireContext(), "order Rejected successfully.")
            } else {
                ToastUtility.errorToast(
                    requireContext(),
                    "Failed to confirm order. Please try again."
                )
            }
        }

    }



    private fun initClickListeners() {

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnUpload.setOnClickListener {
            findNavController().navigate(R.id.action_allOrdersFragment_to_viewPrescriptionFragment)
        }
    }


    private fun getAddress(latitude: Double, longitude: Double): String {
        val addresses: List<Address>
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        )!!
        val address: String = addresses[0].getAddressLine(0)

        Log.i("Address", "${address}")
        return address
    }

}
