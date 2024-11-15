package com.hash.medmarket.ui.client.fragments.home.medicines

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.FragmentClientMedicineBinding
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.client.fragments.home.adapter.MedicineAdapter
import com.hash.medmarket.utils.Cart
import com.hash.medmarket.utils.CartManager
import com.hash.medmarket.utils.ToastUtility
import java.util.Locale

class ClientMedicineFragment : Fragment() {

    private lateinit var binding: FragmentClientMedicineBinding
    private lateinit var adapter: MedicineAdapter
    private lateinit var viewModel: ClientMedicineViewModel

    private var selectedMedicineId: String? = null

    private var storeId: String? = null
    private var selectedMedicine: Medicine? = null
    private var users: Users? = null
    private var cart: Cart? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientMedicineBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[ClientMedicineViewModel::class.java]
        cart = Cart.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        selectedMedicine = arguments?.getSerializable("selected_medicine") as? Medicine
        selectedMedicineId = arguments?.getString("selected_medicine_id") ?: ""

        users = arguments?.getSerializable("owner") as? Users

//        ownerId = arguments?.getString("owner_id") ?: ""
        cart!!.medicines.clear()

        if (selectedMedicine != null) {

            binding.btnAddtoCart.visibility = View.VISIBLE

            viewModel.getMedicine(selectedMedicine!!.storeId!!)
            viewModel.getStoreData(selectedMedicine!!.storeId!!)
            viewModel.storeData.observe(viewLifecycleOwner, Observer { storeData ->
                binding.imageStore.load(storeData["image"] as? String)
                binding.textStoreName.text = storeData["name"] as? String
                binding.textPhoneNumber.text = storeData["phone"] as? String
                try {
                    val location = storeData["location"] as? String
                    val parts = location?.split(",")
                    val latitude = parts!![0].toDouble()
                    val longitude = parts[1].toDouble()
                    val address = getAddress(latitude, longitude)
                    if (address != null) {
                        binding.textStoreAddress.text = address
                    }
                } catch (e: Exception) {
                    // Handle the exception
                }
            })

            observeMedicines()
        }
        else{
            binding.btnAddtoCart.visibility = View.GONE

        }


        if (users != null) {
//            binding.tvTitle.text=users!!.name
            binding.imageStore.load(users!!.image)
            binding.textStoreName.text = users!!.name
            binding.textPhoneNumber.text = users!!.phone

            try {
                val parts = users!!.location!!.split(",")
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                val address = getAddress(latitude, longitude)
                binding.textStoreAddress.text = address
            } catch (e: Exception) {
                // Handle the exception
            }

            observeMedicinesForStore()
        }

//            observeMedicinesAll()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnUpload.setOnClickListener {
            val bundle = Bundle().apply {
                putString("store_id", storeId)
                putString("owner_id", storeId)
            }
            findNavController().navigate(
                R.id.action_clientMedicineFragment_to_prescriptionFragment,
                bundle
            )
        }
        binding.btnAddtoCart.setOnClickListener {
            if (selectedMedicineId == null) {
                ToastUtility.errorToast(requireContext(), "Please select a medicine.")
            } else {
                CartManager.clearCartData(requireContext())
                CartManager.saveCartData(requireContext(), cart!!)
                val bundle = Bundle().apply {
                    putString("owner_id", storeId)
                }

                findNavController().navigate(
                    R.id.action_clientMedicineFragment_to_checkoutFragment,
                    bundle
                )
            }
        }




    }


    private var selectedMedicineIds = mutableSetOf<String>()

    private fun observeMedicines() {

        storeId = selectedMedicine!!.storeId
        waitingDialog.show("Getting Medicine")


        viewModel.medicines.observe(viewLifecycleOwner, Observer { medicines ->
            waitingDialog.dismiss()
            if (medicines.isNotEmpty()) {
                adapter = MedicineAdapter(medicines, selectedMedicineIds, ::onMedicineClick)
                selectedMedicineId?.let {
                    adapter.setSelectedMedicine(it)
                }

                binding.rvMedicine.adapter = adapter
                cart = Cart.getInstance()
                if (cart!!.medicines.none { it.medicineId == selectedMedicineId }) {
                    medicines.find { it.medicineId == selectedMedicineId }
                        ?.let { cart!!.addMedicine(it) }
//                    binding.tvCartCount.text = "${cart.medicines.size}"
                }
                binding.emptyData.visibility = View.GONE
            } else {
                binding.rvMedicine.adapter = null
                binding.emptyData.visibility = View.VISIBLE
            }
        })
    }

    private fun observeMedicinesForStore() {
        val store = users!!.userId.toString()
        storeId = store

        waitingDialog.show("Getting Medicine")
        if (store.isNotEmpty()) {
            viewModel.getMedicine(store)
            viewModel.medicines.observe(viewLifecycleOwner, Observer { medicine ->
                waitingDialog.dismiss()
                if (medicine.isNotEmpty()) {
                    binding.emptyData.visibility = View.GONE
                    adapter = MedicineAdapter(medicine, selectedMedicineIds, ::onMedicineClick)

                    binding.btnAddtoCart.visibility = View.VISIBLE

                    binding.rvMedicine.adapter = adapter
                } else {
                    binding.emptyData.visibility = View.VISIBLE
                }
            })
        } else {
            waitingDialog.dismiss()
            binding.emptyData.visibility = View.VISIBLE
            binding.btnAddtoCart.visibility = View.GONE

//            ToastUtility.errorToast(requireContext(), "No Medicine Found.")
        }
    }


    private fun onMedicineClick(medicine: Medicine, selectedMedicine: String, position: Int) {
//        cart?.medicines?.clear()

        if (!cart!!.medicines.contains(medicine)) {
            cart!!.addMedicine(medicine)
        } else {
            cart!!.removeMedicine(medicine)
        }
    }


    val waitingDialog by lazy { WaitingDialog(requireContext()) }
    override fun onDestroyView() {
        waitingDialog.dismiss()
        super.onDestroyView()
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

        return address
    }

//    override fun onResume() {
//        super.onResume()
//        cart!!.medicines.clear()
//    }

}