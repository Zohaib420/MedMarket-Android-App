package com.hash.medmarket.ui.client.fragments.home.prescription

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Timestamp
import com.hash.medmarket.database.model.Prescription
import com.hash.medmarket.database.model.Store
import com.hash.medmarket.databinding.FragmentPrescriptionBinding
import com.hash.medmarket.ui.admin.activities.FullScreenImageActivity
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrescriptionFragment : Fragment() {

    private lateinit var binding: FragmentPrescriptionBinding

    private lateinit var viewModel: PrescriptionViewModel

    private lateinit var adapter: PrescAdapter

    private var ownerId: String? = null
    private var storeId: String? = null

    private var selectedStore: Store? = null


    private var imageUri: Uri? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrescriptionBinding.inflate(layoutInflater, container, false)
        viewModel = PrescriptionViewModel()

        ownerId = arguments?.getSerializable("owner_id") as? String
        storeId = arguments?.getSerializable("store_id") as? String

        Log.d("Owner Id","$ownerId")
        Log.d("Store Data","$storeId")

        viewModel.prescription.storeId = storeId


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observePrescriptions()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.addPhoto.setOnClickListener {
            initLaunchers()
        }
        binding.btnSend.setOnClickListener {
            addImage()
        }


    }


    val waitingDialog by lazy { WaitingDialog(requireContext()) }
    override fun onDestroyView() {
        waitingDialog.dismiss()
        super.onDestroyView()
    }

    private fun addImage() {

        waitingDialog.show("Sending Prescription")
        val user = UserAuthManager.getUserData(requireContext())

        val timestamp = Timestamp.now().toDate()
        val dateFormat =
            SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val formattedDate = dateFormat.format(timestamp)


        viewModel.prescription.userName = user?.name
        viewModel.prescription.userId = user?.userId
        viewModel.prescription.location = user?.location
        viewModel.prescription.phone = user?.phone
        viewModel.prescription.timestamp = formattedDate
        viewModel.prescription.status = "pending"
        viewModel.prescription.pharmacistId = ownerId


        viewModel.uploadImageSavePrescription(viewModel.prescription) { onSuccess ->
            waitingDialog.dismiss()
            if (onSuccess) {
                ToastUtility.successToast(requireContext(), "Prescription sent successfully.")
                findNavController().navigateUp()
            } else {
                ToastUtility.errorToast(requireContext(), "Failed to send prescription. Please try again.")
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
            binding.addPhoto.setImageURI(imageUri)
            binding.btnSend.visibility=View.VISIBLE
        } else {
            // Handle the case where the result is not OK or data is null
            // You might want to show a message to the user or take appropriate action
        }
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
                    adapter = PrescAdapter(
                        requireContext(),
                        order,
                        ::onConfirmClick,
                        ::onRejectClick,
                        ::onImageClick
                    )
                    binding.rvPrescriptions.adapter = adapter
                } else {
                    binding.emptyData.visibility = View.VISIBLE
//                    ToastUtility.errorToast(requireContext(), "No Order Found.")
                }
            })
        } else {
            waitingDialog.dismiss()
            binding.emptyData.visibility = View.VISIBLE
//            ToastUtility.errorToast(requireContext(), "No Order Found.")
        }
    }

    private fun onConfirmClick(order: Prescription,position: Int) {

    }
    private fun onRejectClick(order: Prescription, position: Int) {

    }

    private fun onImageClick(imageUrl: String) {
        val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
        intent.putExtra("image_url", imageUrl)
        startActivity(intent)
    }




}
