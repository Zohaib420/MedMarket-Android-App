package com.hash.medmarket.ui.pharmacists.fragments.medicine.addNew

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.databinding.FragmentAddNewMedicineBinding
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNewMedicineFragment : Fragment() {

    private lateinit var binding: FragmentAddNewMedicineBinding
    private lateinit var viewModel: AddNewMedicineViewModel
    private var waitingDialog: WaitingDialog? = null
    private var medicineId: String?=null
    private var status: String?=null
    private lateinit var navController: NavController

    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewMedicineBinding.inflate(layoutInflater, container, false)
        viewModel = AddNewMedicineViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= findNavController()

        arguments?.let { bundle ->
            val medicine = bundle.getSerializable("selected_medicine") as? Medicine
            populateUIWithMedicine(medicine)
        }
//        selectedStore ?: run {
//            ToastUtility.errorToast(requireContext(), "Selected store is null.")
//            findNavController().navigateUp()
//            return
//        }

        viewModel.medicine.ownerId=UserAuthManager.getUserData(requireContext())!!.userId


        viewModel.medicine.storeId = FirebaseAuth.getInstance().currentUser!!.uid

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            validateInputs()
        }

        binding.tvSelectCategory.setOnClickListener {
            showCategoryPopupMenu()
        }
        binding.btnPickProfile.setOnClickListener {
            initLaunchers()
        }
    }

    private fun populateUIWithMedicine(medicine: Medicine?) {
        medicine?.let {
            binding.etName.setText(it.name)
            binding.etDescription.setText(it.description)
            binding.etPrice.setText(it.price)
            binding.etQuantity.setText(it.quantity)
            binding.tvSelectCategory.text = it.category
            viewModel.medicine.category=it.category
            binding.ivMedicine.load(it.image)
            binding.btnPickProfile.visibility=View.GONE
            binding.btnSave.text = "Update"
            binding.tvTitle.text = "Update Medicine"
            medicineId=it.medicineId
            viewModel.medicine.medicineId=medicineId
            viewModel.medicine.ownerId=it.ownerId
            viewModel.medicine.image=it.image
            status="Updating Medicine"
        }
    }

    private fun showCategoryPopupMenu() {
        val categories = arrayOf("Analgesics", "Antibiotics", "Antipyretics", "Antihistamines")
        val popupMenu = PopupMenu(requireContext(), binding.tvSelectCategory)
        for (category in categories) {
            popupMenu.menu.add(category)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            binding.tvSelectCategory.text = menuItem.title
            viewModel.medicine.category = menuItem.title.toString()
            true
        }
        popupMenu.show()
    }

    @SuppressLint("RestrictedApi")
    private fun validateInputs() {
        val name = binding.etName.text.toString()
        val description = binding.etDescription.text.toString()
        val price = binding.etPrice.text.toString()
        val quantity = binding.etQuantity.text.toString()
        if (name.isEmpty()) {
            binding.etName.error = "Please enter the name."
        } else if (description.isEmpty()) {
            binding.etDescription.error = "Please enter the description."
        } else if (price.isEmpty()) {
            binding.etPrice.error = "Please enter the price."
        } else if (quantity.isEmpty()) {
            binding.etQuantity.error = "Please enter the quantity."
        }

        else if (medicineId==null && imageUri==null) {
            ToastUtility.errorToast(requireContext(), "Please select medicine image")
        }else {

            showWaitingDialog()
            val formattedDate =
                SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            viewModel.medicine.apply {
                this.price = price
                this.quantity = quantity
                this.timeStamp = formattedDate
                this.name = name
                this.description = description
            }
            viewModel.sameMedicineImageAndData { isSuccess ->
                dismissWaitingDialog()
                if (isSuccess) {
                    Log.e("Bck",findNavController().currentBackStack.value.toString())
                    ToastUtility.successToast(requireActivity(), "Medicine saved successfully.")
                    findNavController().navigateUp()
                } else {
                    ToastUtility.errorToast(
                        requireActivity(),
                        "Failed to save medicine. Please try again."
                    )
                }
            }
        }
    }

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
            imageUri = data.data
            viewModel.imageUri = imageUri
            binding.ivMedicine.setImageURI(imageUri)
        } else {
            // Handle the case where the result is not OK or data is null
            // You might want to show a message to the user or take appropriate action
        }
    }

    private fun showWaitingDialog() {
        waitingDialog = WaitingDialog(requireContext())
        if (binding.btnSave.text == "Update") {
            waitingDialog?.show("Updating medicine")
        } else {
            waitingDialog?.show("Saving medicine")
        }
    }

    private fun dismissWaitingDialog() {
        waitingDialog?.dismiss()
        waitingDialog = null
    }
}
