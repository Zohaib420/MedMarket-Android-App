package com.hash.medmarket.ui.client.fragments.myOrders

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
import com.hash.medmarket.databinding.FragmentMyOrdersBinding
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.client.fragments.myOrders.adapter.OrdersAdapter
import com.hash.medmarket.utils.UserAuthManager
import java.util.Locale

class MyOrdersFragment : Fragment() {

    private lateinit var binding: FragmentMyOrdersBinding

    private lateinit var adapter: OrdersAdapter

    private lateinit var viewModel: ClientOrderViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        viewModel = ClientOrderViewModel()
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
                    adapter = OrdersAdapter(order, onClick = { myOrder, total, location ->

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
                            .setMessage("Total Bill : $total PKR\nNo of medicines : ${myOrder.medicines.size}\nMedicines : ${medicineList}\nDate : ${myOrder.timestamp}\nAddress : $address")
                            .show()

                    })
                    binding.myOrdersRecyclerView.adapter = adapter
                } else {
                    binding.emptyData.visibility = View.VISIBLE
                }
            })
        } else {
            waitingDialog.dismiss()
            binding.emptyData.visibility = View.VISIBLE
        }
    }


    private fun initClickListeners() {

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
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
