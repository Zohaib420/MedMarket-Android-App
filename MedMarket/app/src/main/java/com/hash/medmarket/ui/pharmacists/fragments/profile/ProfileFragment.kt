package com.hash.medmarket.ui.pharmacists.fragments.profile

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.hash.medmarket.R
import com.hash.medmarket.databinding.FragmentProfileBinding
import com.hash.medmarket.ui.auth.activities.AuthActivity
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.map.MapsActivity
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.util.Locale
import java.util.UUID

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var waitingDialog: WaitingDialog

    private var locationCoordinates = ""
    private var profileImageUri: Uri? = null
    private val storageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initProgressDialog()
        val userData = UserAuthManager.getUserData(requireContext())
        userData?.let { user ->
            binding.etName.setText(user.name)
            binding.etEmailAddress.setText(user.email)
            locationCoordinates = user.location.toString()
            binding.ivProfile.load(user.image) {
                R.drawable.store_image
            }
            val parts = user.location!!.split(",")
            val latitude = parts[0]
            val longitude = parts[1]
            binding.etLocation.setText(getAddress(latitude.toDouble(), longitude.toDouble()))
        }

    }

    private fun initProgressDialog() {
        waitingDialog = WaitingDialog(requireContext())
    }

    private fun initClickListeners() {

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            UserAuthManager.clearUserData(requireContext())
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.etLocation.setOnClickListener {
            startActivityForResult(Intent(requireContext(), MapsActivity::class.java), 123)
        }

        binding.btnUpdate.setOnClickListener {
            validateInputs()
        }

        binding.btnPickProfile.setOnClickListener {
            requestProfileImage.launch("image/*")
        }
    }

    private fun validateInputs() {

        val name = binding.etName.text.toString()
        val location = binding.etLocation.text.toString()

        if (name.isEmpty()) {
            binding.etName.error = "Please enter your name."
        } else if (location.isEmpty()) {
            binding.etLocation.error = "Please enter your location."
        } else {

            if (profileImageUri != null) {

                waitingDialog.show("Updating...")
                binding.etLocation.isFocusable = true
                binding.etLocation.isFocusableInTouchMode = true

                val profileImageFileName = UUID.randomUUID().toString()
                val profileImageRef = storageRef.child("images/$profileImageFileName.jpg")
                val profileUploadTask = profileImageRef.putFile(profileImageUri!!)

                profileUploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    profileImageRef.downloadUrl
                }

                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            val imageURL = downloadUri.toString()

                            FirebaseFirestore.getInstance().collection("Users")
                                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                                .update(
                                    "name", name, "location", locationCoordinates,
                                    "image", imageURL
                                )
                                .addOnSuccessListener {
                                    waitingDialog.dismiss()
                                    ToastUtility.successToast(
                                        requireContext(),
                                        "Updated"
                                    )
                                }
                                .addOnFailureListener {
                                    waitingDialog.dismiss()
                                    ToastUtility.errorToast(requireContext(), it.message.toString())
                                }

                        } else {

                            waitingDialog.dismiss()
                            ToastUtility.errorToast(
                                requireContext(),
                                task.exception!!.message.toString()
                            )
                        }

                    }
                    .addOnFailureListener {
                        waitingDialog.dismiss()
                        ToastUtility.errorToast(requireContext(), it.message.toString())

                    }

            } else {
                waitingDialog.show("Updating...")
                binding.etLocation.isFocusable = true
                binding.etLocation.isFocusableInTouchMode = true
                FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("name", name, "location", locationCoordinates)
                    .addOnSuccessListener {
                        waitingDialog.dismiss()
                        ToastUtility.successToast(
                            requireContext(),
                            "Updated"
                        )
                    }
                    .addOnFailureListener {
                        waitingDialog.dismiss()
                        ToastUtility.errorToast(requireContext(), it.message.toString())
                    }
            }

//            binding.etLocation.isFocusable = true
//            binding.etLocation.isFocusableInTouchMode = true
//            userData!!.name = name
//            userData!!.location = locationCoordinates

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        locationCoordinates = data!!.getStringExtra("LOCATION").toString()
        Log.i("Data Location", "$locationCoordinates")
        val parts = locationCoordinates!!.split(",")
        val latitude = parts[0]
        val longitude = parts[1]
        binding.etLocation.setText(getAddress(latitude.toDouble(), longitude.toDouble()))

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

    override fun onResume() {
        super.onResume()
        profileImageUri?.let { uri ->
            binding.ivProfile.setImageURI(uri)
        }

    }

    private val requestProfileImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                profileImageUri = uri
                binding.ivProfile.setImageURI(uri)
            }
        }

}
