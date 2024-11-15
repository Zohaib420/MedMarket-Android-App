package com.hash.medmarket.ui.pharmacists.fragments.home

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Timestamp
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Store
import com.hash.medmarket.databinding.FragmentPharmcistHomeBinding
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.map.MapsActivity
import com.hash.medmarket.ui.pharmacists.activities.PharmacistDrawerListener
import com.hash.medmarket.ui.pharmacists.fragments.home.adapter.AllStoresAdapter
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PharmacistHomeFragment : Fragment() {

    private lateinit var binding: FragmentPharmcistHomeBinding


    private lateinit var waitingDialog: WaitingDialog

    private var dialogBinding: View? = null

    private lateinit var viewModel: PharmacistHomeViewModel

    private lateinit var storeAdapter: AllStoresAdapter

    private var imageUri: Uri? = null
    private var storeData: Store? = null


    private lateinit var etAddress: EditText
    private var locationCoordinates = "0,0"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPharmcistHomeBinding.inflate(inflater, container, false)
        viewModel = PharmacistHomeViewModel()
        initStore()
        initProgressDialog()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        observeStores()
        val ownerName = UserAuthManager.getUserData(requireContext())?.name
        binding.tvTitle.text=ownerName

    }


    private fun initStore() {
        binding.rvStores
    }

    private fun observeStores() {
        waitingDialog.show("Getting Stores")
        val ownerId = UserAuthManager.getUserData(requireContext())?.userId
        if (!ownerId.isNullOrEmpty()) {
            viewModel.getStoresByOwnerId(ownerId)
            viewModel.stores.observe(viewLifecycleOwner, Observer { stores ->
                waitingDialog.dismiss()
                if (stores.isNotEmpty()) {
                    binding.emptyData.visibility = View.GONE
                    storeAdapter = AllStoresAdapter(stores, ::onStoreClick, ::onMenuClick)
                    binding.rvStores.adapter = storeAdapter
                } else {
                    binding.emptyData.visibility = View.VISIBLE
                    storeAdapter = AllStoresAdapter(stores, ::onStoreClick, ::onMenuClick)
                    binding.rvStores.adapter = storeAdapter
                    ToastUtility.errorToast(requireContext(), "No Store Found.")
                }
            })
        } else {
            waitingDialog.dismiss()
            binding.emptyData.visibility = View.VISIBLE
            ToastUtility.errorToast(requireContext(), "No Store Found.")
        }
    }


    private fun initClickListeners() {

//        binding.notificationImage.setOnClickListener {
//            findNavController().navigate(R.id.action_pharmacistHomeFragment_to_notificationFragment)
//        }

        binding.navImage.setOnClickListener {
            (requireActivity() as PharmacistDrawerListener).openDrawer()
        }

        binding.btnAdd.setOnClickListener {
            showStoreDialog()
        }

    }

    private fun showStoreDialog() {

        val dialog = Dialog(requireContext())
        dialogBinding = layoutInflater.inflate(R.layout.dialog_input_stores, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogBinding!!)
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        val crossBtn = dialogBinding?.findViewById<ImageView>(R.id.dialog_cross)
        val etName = dialogBinding?.findViewById<EditText>(R.id.etName)
        etAddress = dialogBinding?.findViewById(R.id.etAddress)!!
        val etEmail = dialogBinding?.findViewById<EditText>(R.id.et_email_address)
        val etPhone = dialogBinding?.findViewById<EditText>(R.id.et_phone)
        val btnAddStore = dialogBinding?.findViewById<Button>(R.id.btnAddStore)
        val btnPickProfile = dialogBinding?.findViewById<ImageView>(R.id.btnPickProfile)
        val storeImage = dialogBinding?.findViewById<ImageView>(R.id.ivStore)


        btnPickProfile!!.setOnClickListener{
            initLaunchers()
        }

        if (storeData != null) {
            etName?.setText(storeData!!.name)
            etAddress?.setText(storeData!!.address)
            etEmail?.setText(storeData!!.email)
            etPhone?.setText(storeData!!.phoneNumber)
            storeImage?.load(storeData!!.image)
            btnPickProfile.visibility = View.INVISIBLE
            btnAddStore?.text = "Update"
        }

        etAddress?.setOnClickListener {
            startActivityForResult(Intent(requireContext(), MapsActivity::class.java), 123)
        }

        btnAddStore?.setOnClickListener {
            if (storeData == null) {
                waitingDialog.show("Adding Store")
                val name = etName?.text.toString()
                val address = etAddress?.text.toString()
                val email = etEmail?.text.toString()
                val phone = etPhone?.text.toString()


                if (name.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty() || imageUri==null) {
                    ToastUtility.errorToast(requireContext(), "Please fill in all fields")
                    waitingDialog.dismiss()
                    return@setOnClickListener
                }

                val ownerId = UserAuthManager.getUserData(requireContext())?.userId

                viewModel.store.ownerId = ownerId
                viewModel.store.name = name
                viewModel.store.email = email
                viewModel.store.phoneNumber = phone
                viewModel.store.address = locationCoordinates
                val timestamp = Timestamp.now().toDate()
                val dateFormat =
                    SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                val formattedDate = dateFormat.format(timestamp)

                viewModel.store.timestamp = formattedDate

                viewModel.saveStoreImage { isSuccess ->
                    waitingDialog.dismiss()
                    dialog.dismiss()
                    if (isSuccess) {
                        observeStores()
                        ToastUtility.successToast(requireContext(), "Store added successfully.")
                    } else {
                        ToastUtility.errorToast(
                            requireContext(),
                            "Failed to add store. Please try again."
                        )
                    }
                }

            } else {
                waitingDialog.show("Updating Store")
                val name = etName?.text.toString()
                val address = etAddress?.text.toString()
                val email = etEmail?.text.toString()
                val phone = etPhone?.text.toString()


                if (name.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    ToastUtility.errorToast(requireContext(), "Please fill in all fields")
                    waitingDialog.dismiss()
                    return@setOnClickListener
                }

                val ownerId = UserAuthManager.getUserData(requireContext())?.userId

                storeData!!.ownerId = ownerId
                storeData!!.name = name
                storeData!!.email = email
                storeData!!.phoneNumber = phone
                storeData!!.address = address
                val timestamp = Timestamp.now().toDate()
                val dateFormat =
                    SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                val formattedDate = dateFormat.format(timestamp)

                storeData!!.timestamp = formattedDate

                viewModel.updateStore(storeData!!) { success ->
                    waitingDialog.dismiss()
                    dialog.dismiss()
                    if (success) {
                        ToastUtility.successToast(requireContext(), "Store updated successfully.")
                        observeStores()
                    } else {
                        ToastUtility.errorToast(requireContext(), "Failed to update store. Please try again.")
                    }
                }
            }
            dialog.dismiss()
        }


        crossBtn?.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun initProgressDialog() {

        waitingDialog = WaitingDialog(requireContext())
    }

    private fun onStoreClick(store: Store) {
        val bundle = Bundle().apply {
            putSerializable("selected_store", store)
        }
//        findNavController().navigate(
//            R.id.action_pharmacistHomeFragment_to_allMedicineFragment,
//            bundle
//        )

    }
    private fun onMenuClick(store: Store, position: Int) {
        val options = arrayOf("Edit", "Delete")
        storeData=store
        AlertDialog.Builder(requireContext())
            .setTitle("Options")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showStoreDialog()
                    1 -> onDeleteClick(store)
                }
                dialog.dismiss()
            }
            .show()
    }



    private fun onDeleteClick(store: Store) {
        waitingDialog.show("Deleting Store")
        viewModel.deleteMedicine(store.storeId!!)
        viewModel.deleteStoreResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            waitingDialog.dismiss()
            if (isSuccess) {
                observeStores()
                ToastUtility.successToast(requireContext(), "Store deleted successfully.")
            } else {
                ToastUtility.errorToast(
                    requireContext(),
                    "Failed to delete store. Please try again."
                )
            }
        })
    }

//    private fun onEditClick(store: Store) {
//        val bundle = Bundle().apply {
//            putSerializable("selected_store", store)
//        }
//
//    }


    private fun initLaunchers() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (data.hasExtra("LOCATION")) {
                locationCoordinates = data.getStringExtra("LOCATION").toString()
                Log.i("Data Location", "$locationCoordinates")
                val parts = locationCoordinates!!.split(",")
                val latitude = parts[0]
                val longitude = parts[1]
                etAddress.setText(getAddress(latitude.toDouble(), longitude.toDouble()))
            } else {
                imageUri = data.data
                viewModel.imageUri = imageUri
                dialogBinding?.findViewById<ImageView>(R.id.ivStore)?.setImageURI(imageUri)
            }
        } else {
            // Handle the case where the result is not OK or data is null
            // You might want to show a message to the user or take appropriate action
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
