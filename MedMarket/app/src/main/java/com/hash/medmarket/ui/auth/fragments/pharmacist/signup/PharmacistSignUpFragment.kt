package com.hash.medmarket.ui.auth.fragments.pharmacist.signup

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Timestamp
import com.google.firebase.messaging.FirebaseMessaging
import com.hash.medmarket.R
import com.hash.medmarket.databinding.FragmentPharmacistSignupBinding
import com.hash.medmarket.ui.auth.activities.PendingPharmaActivity
import com.hash.medmarket.ui.auth.dialog.ProgressDialog
import com.hash.medmarket.ui.map.MapsActivity
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.text.SimpleDateFormat
import java.util.Locale


class PharmacistSignUpFragment : Fragment() {

    private lateinit var binding: FragmentPharmacistSignupBinding

    private lateinit var progressDialog: ProgressDialog

    private lateinit var viewModel: PharmacistSignUpViewModel


    private var profileImageUri: Uri? = null
    private var licenseImageUri: Uri? = null
    private var locationCoordinates = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPharmacistSignupBinding.inflate(inflater, container, false)
        viewModel = PharmacistSignUpViewModel()
        initProgressDialog()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initClickListeners()
    }

    override fun onResume() {
        super.onResume()
//        binding.etName.setText("")
//        binding.etEmailAddress.setText("")
//        binding.etPassword.setText("")
        profileImageUri?.let { uri ->
            binding.ivProfile.setImageURI(uri)
        }
        licenseImageUri?.let { uri ->
            binding.addPhotoLicense.setImageURI(uri)
        }
    }

    private fun initClickListeners() {


        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_pharmacistSignUpFragment_to_loginFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSignUp.setOnClickListener {
            validateInputs()
        }
        binding.btnPickProfile.setOnClickListener {
            requestProfileImage.launch("image/*")
        }

        binding.pick.setOnClickListener {
            requestLicenseImage.launch("image/*")
            binding.pick.visibility = View.VISIBLE
        }

        binding.etLocation.setOnClickListener {
            startActivityForResult(Intent(requireContext(), MapsActivity::class.java), 123)
        }

    }


    private fun initProgressDialog() {

        progressDialog = ProgressDialog(requireContext())

    }

    private fun validateInputs() {

        val name = binding.etName.text.toString()
        val email = binding.etEmailAddress.text.toString()
        val password = binding.etPassword.text.toString()

        if (name.isEmpty()) {
            binding.etName.error = "Please enter your name."
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailAddress.error = "Email is empty or invalid."
        } else if (password.isEmpty() || password.length < 6) {
            binding.etPassword.error = "Password is too short or empty."
        } else if (profileImageUri == null) {
            ToastUtility.errorToast(requireContext(), "Select profile Image")
            return
        } else if (licenseImageUri == null) {
            ToastUtility.errorToast(requireContext(), "Select license Image")
            return
        } else if (TextUtils.isEmpty(binding.etLocation.text.toString())) {
            ToastUtility.errorToast(requireContext(), "Select location please")
            return
        } else if (TextUtils.isEmpty(binding.etPhone.text.toString())) {
            ToastUtility.errorToast(requireContext(), "Select phone Number please")
            return
        } else {

            progressDialog.showDialogue()

            val timestamp = Timestamp.now().toDate()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(timestamp)

            viewModel.user.timeStamp = formattedDate
            viewModel.user.email = email
            viewModel.user.name = name
            viewModel.user.status = ""
            viewModel.user.password = password
            viewModel.user.location = locationCoordinates
            viewModel.user.phone = binding.etPhone.text.toString()

            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                viewModel.user.token = token.toString()

            viewModel.signUpPharmacist(email, password).observe(viewLifecycleOwner) { result ->

            progressDialog.hideDialogue()

                    if (result) {
                        ToastUtility.successToast(
                            requireContext(),
                            "Pharmacist Signup Successful. Please verify your account & wait for account approval."
                        )
                        UserAuthManager.saveUserData(requireContext(), viewModel.user)
                        startActivity(
                            Intent(
                                requireContext(),
                                PendingPharmaActivity::class.java
                            ).putExtra("status", "").putExtra("password", password)
                        )
                        requireActivity().finish()
                    } else {
                        ToastUtility.errorToast(requireContext(), "Error : Signup Failed")
                    }


            }

        }


        }
            .addOnFailureListener {
                ToastUtility.errorToast(requireContext(), it.message.toString())
            }

    }


    private fun initLaunchers() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    private val requestProfileImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                profileImageUri = uri
                binding.ivProfile.setImageURI(uri)
                viewModel.imageUri = uri
            }
        }

    private val requestLicenseImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                licenseImageUri = uri
                binding.addPhotoLicense.setImageURI(uri)
                viewModel.imageUriLicesne = uri
            }
        }


    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.fragment.app.Fragment"
        )
    )

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

}