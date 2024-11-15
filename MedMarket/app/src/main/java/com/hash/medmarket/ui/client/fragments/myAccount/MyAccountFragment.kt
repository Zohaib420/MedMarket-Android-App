package com.hash.medmarket.ui.client.fragments.myAccount

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.FragmentMyAccountBinding
import com.hash.medmarket.ui.auth.activities.AuthActivity
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.map.MapsActivity
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.util.Locale

class MyAccountFragment : Fragment() {

    private lateinit var binding: FragmentMyAccountBinding

    private lateinit var viewModel: MyAccountViewModel

    private lateinit var waitingDialog: WaitingDialog

    private var userData: Users? = null

    private var locationCoordinates = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        viewModel = MyAccountViewModel()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initProgressDialog()
        userData = UserAuthManager.getUserData(requireContext())
        userData?.let { user ->
            binding.etName.setText(user.name)
            binding.etEmailAddress.setText(user.email)
            locationCoordinates = user.location.toString()
            val parts = user.location!!.split(",")
            val latitude = parts[0]
            val longitude = parts[1]
            binding.etLocation.setText(getAddress(latitude.toDouble(), longitude.toDouble()))
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
            binding.etLocation.isFocusable = true
            binding.etLocation.isFocusableInTouchMode = true
            userData!!.name=name
            userData!!.location = locationCoordinates
            updateUser()
        }
    }

    private fun updateUser() {
        waitingDialog.show("updating user")
        viewModel.updateUser(userData!!) { success ->
            waitingDialog.dismiss()
            if (success) {
                userData!!.name = binding.etName.text.toString()
                userData!!.location = locationCoordinates
                UserAuthManager.saveUserData(requireContext(), userData!!)
                ToastUtility.successToast(requireContext(), "User updated successfully.")
                updateUi()
            } else {
                ToastUtility.errorToast(requireContext(), "Failed to update user. Please try again.")
            }
        }

    }

    private fun updateUi() {
        val userData = UserAuthManager.getUserData(requireContext())
        userData?.let { user ->
            binding.etName.setText(user.name)
            binding.etEmailAddress.setText(user.email)
            val parts = user.location!!.split(",")
            val latitude = parts[0]
            val longitude = parts[1]
            binding.etLocation.setText(getAddress(latitude.toDouble(), longitude.toDouble()))
        }
    }

    private fun initClickListeners() {

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etLocation.setOnClickListener {
            startActivityForResult(Intent(requireContext(), MapsActivity::class.java), 123)
        }

        binding.btnUpdate.setOnClickListener {
            validateInputs()
        }
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            UserAuthManager.clearUserData(requireContext())
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.etName.setOnClickListener {
            binding.etName.isFocusable = true
            binding.etName.isFocusableInTouchMode = true
            binding.btnUpdate.visibility = View.VISIBLE
        }
    }

    private fun initProgressDialog() {
        waitingDialog = WaitingDialog(requireContext())
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


}
