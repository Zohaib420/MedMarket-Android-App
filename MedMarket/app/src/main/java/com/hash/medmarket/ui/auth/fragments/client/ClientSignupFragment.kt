package com.hash.medmarket.ui.auth.fragments.client

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.hash.medmarket.R
import com.hash.medmarket.databinding.FragmentClientSignupBinding
import com.hash.medmarket.ui.auth.dialog.ProgressDialog
import com.hash.medmarket.ui.client.activities.MainClientActivity
import com.hash.medmarket.ui.map.MapsActivity
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.text.SimpleDateFormat
import java.util.Locale

class ClientSignupFragment : Fragment() {

    private lateinit var binding: FragmentClientSignupBinding

    private lateinit var progressDialog: ProgressDialog

    private lateinit var viewModel: ClientSignupViewModel

    private var locationCoordinates = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientSignupBinding.inflate(inflater, container, false)
        viewModel= ClientSignupViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressDialog()

        initClickListeners()


    }


    private fun initClickListeners() {


        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_clientSignupFragment_to_loginFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSignUp.setOnClickListener {
            validateInputs()
        }
        binding.etLocation.setOnClickListener {
            startActivityForResult(Intent(requireContext(), MapsActivity::class.java), 123)
        }



    }

    private fun validateInputs() {
        val name = binding.etName.text.toString()
        val email = binding.etEmailAddress.text.toString()
        val password = binding.etPassword.text.toString()
        val phoneNumber = binding.etPhoneNumber.text.toString()
        if (name.isEmpty()) {
            binding.etName.error = "Please enter your name."
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailAddress.error = "Email is empty or invalid."
        } else if(phoneNumber.isEmpty()) {
            binding.etPhoneNumber.error = "Phone Number is empty or invalid."
        } else if (password.isEmpty() || password.length < 6) {
            binding.etPassword.error = "Password is too short or empty."
        }
        else if(TextUtils.isEmpty(binding.etLocation.text.toString())){
            ToastUtility.errorToast(requireContext(), "Select location please")
            return
        }
        else {

            progressDialog.showDialogue()

            val timestamp = Timestamp.now().toDate()
            val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(timestamp)

            viewModel.user.timeStamp=formattedDate
            viewModel.user.phone=phoneNumber
            viewModel.user.email=email
            viewModel.user.name=name
            viewModel.user.password = password
            viewModel.user.location = locationCoordinates


            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                viewModel.user.token = token.toString()

            viewModel.signUpClient(email, password).observe(viewLifecycleOwner) { result ->

                FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()

                progressDialog.hideDialogue()
                    if (result) {
                        ToastUtility.successToast(requireContext(), "Verify your email to continue")
                        UserAuthManager.saveUserData(requireContext(),viewModel.user)
                        findNavController().navigateUp()
                        FirebaseAuth.getInstance().signOut()
//                        gotoClientScreen()

                    } else {
                        ToastUtility.errorToast(requireContext(), "Error : Signup Failed")
                    }
            }

            }.addOnFailureListener {
                progressDialog.hideDialogue()
                ToastUtility.errorToast(requireContext(), it.message.toString())
            }
        }
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
    }

    private fun gotoClientScreen() {
        Intent(requireContext(), MainClientActivity::class.java).apply {
            startActivity(this)
            activity?.finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            locationCoordinates = data!!.getStringExtra("LOCATION").toString()
            Log.i("Data Location", "$locationCoordinates")
            val parts = locationCoordinates!!.split(",")
            val latitude = parts[0]
            val longitude = parts[1]
            binding.etLocation.setText(getAddress(latitude.toDouble(), longitude.toDouble()))
        }
        catch (e:Exception){}

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