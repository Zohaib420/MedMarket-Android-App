package com.hash.medmarket.ui.auth.fragments.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.FragmentLoginBinding
import com.hash.medmarket.ui.admin.activities.AdminActivity
import com.hash.medmarket.ui.auth.activities.AuthActivity
import com.hash.medmarket.ui.auth.activities.PendingPharmaActivity
import com.hash.medmarket.ui.auth.dialog.ProgressDialog
import com.hash.medmarket.ui.client.activities.MainClientActivity
import com.hash.medmarket.ui.pharmacists.activities.MainPharmacistActivity
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var viewModel: LoginViewModel

    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = LoginViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressDialog()
        initClickListeners()


    }


    private fun gotoClientScreen() {
        Intent(requireContext(), MainClientActivity::class.java).apply {
            startActivity(this)
            activity?.finish()
        }
    }

    private fun initProgressDialog() {

        progressDialog = ProgressDialog(requireContext())

    }


    private fun gotoPharmacistScreen() {
        Intent(requireContext(), MainPharmacistActivity::class.java).apply {
            startActivity(this)
            activity?.finish()
        }
    }

    private fun gotoAdmin() {
        Intent(requireContext(), AdminActivity::class.java).apply {
            startActivity(this)
            activity?.finish()
        }
    }

    private fun initClickListeners() {

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_introFragment)

        }

        binding.btnForgot.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.btnLogin.setOnClickListener {
            validateInputs()
        }

    }


    private fun validateInputs() {
        val email = binding.etEmailAddress.text.toString()
        val password = binding.etPassword.text.toString()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailAddress.error = "Email is empty or invalid."
        } else if (password.isEmpty() || password.length < 6) {
            binding.etEmailAddress.error = null
            binding.etPassword.error = "Password is too short or empty."
        } else {
            progressDialog.showDialogue()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { auth ->

                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->

                        FirebaseFirestore.getInstance().collection("Users")
                            .document(FirebaseAuth.getInstance().currentUser!!.uid)
                            .update("token", token).addOnSuccessListener {

                                FirebaseFirestore.getInstance().collection("Users")
                                    .document(auth.user!!.uid).get().addOnSuccessListener {

                                        progressDialog.hideDialogue()

                                        if (it != null && it.exists()) {

                                            val user = it.toObject(Users::class.java)

                                            UserAuthManager.saveUserData(requireContext(), user!!)

                                            progressDialog.hideDialogue()

                                            // For Pharmacist
                                            if (it.get("userType").toString() == "pharmacist") {
                                                val status = it.get("status").toString()
                                                if (status == "approved") {
                                                    startActivity(
                                                        Intent(
                                                            requireContext(),
                                                            MainPharmacistActivity::class.java
                                                        )
                                                    )
                                                    requireActivity().finishAffinity()

                                                } else {
                                                    startActivity(
                                                        Intent(
                                                            requireContext(),
                                                            PendingPharmaActivity::class.java
                                                        ).putExtra("status", status)
                                                    )
                                                    requireActivity().finishAffinity()
                                                }
                                            }
                                            // For Admin
                                            else if (it.get("userType").toString() == "admin") {
                                                Intent(
                                                    requireContext(), AdminActivity::class.java
                                                ).apply {
                                                    startActivity(this)
                                                    requireActivity().finishAffinity()
                                                }
                                            }

                                            // For Normal USer
                                            else {

                                                if(FirebaseAuth.getInstance().currentUser!!.isEmailVerified){
                                                    Intent(
                                                        requireContext(), MainClientActivity::class.java
                                                    ).apply {
                                                        startActivity(this)
                                                        requireActivity().finishAffinity()
                                                    }
                                                }
                                                else{
                                                    ToastUtility.errorToast(requireContext(),"Email not verified")
                                                }


                                            }

                                        } else {
                                            progressDialog.hideDialogue()

                                            Intent(
                                                requireContext(), AuthActivity::class.java
                                            ).apply {
                                                startActivity(this)
                                                requireActivity().finishAffinity()
                                            }
                                        }

                                    }.addOnFailureListener {
                                        progressDialog.hideDialogue()

                                        ToastUtility.errorToast(
                                            requireContext(), it.message.toString()
                                        )
                                    }

                            }.addOnFailureListener {
                                progressDialog.hideDialogue()
                                ToastUtility.errorToast(requireContext(), it.message.toString())
                            }

                    }.addOnFailureListener {
                        progressDialog.hideDialogue()
                        ToastUtility.errorToast(requireContext(), it.message.toString())
                    }
                }.addOnFailureListener {
                    progressDialog.hideDialogue()
                    ToastUtility.errorToast(requireContext(), it.message.toString())
                }


//            viewModel.loginUser(email, password).observe(viewLifecycleOwner) { result ->
//                progressDialog.hideDialogue()
//                if (result) {
//                    lifecycleScope.launch {
//                        Log.d(
//                            "Login Fragment",
//                            "current user = " + FirebaseAuth.getInstance().currentUser
//                        )
//
//                        val user = viewModel.getUserData()
//                        if (user != null) {
//
//                            Timber.tag("UserData").d("User not null = " + user)
//
//                            UserAuthManager.saveUserData(requireContext(), user)
//                            when (user.userType) {
//                                "pharmacist" -> {
//                                    when (user.status) {
//                                        "approved" -> {
//                                            ToastUtility.successToast(
//                                                requireContext(),
//                                                "Login Pharmacist Successfully."
//                                            )
//                                            gotoPharmacistScreen()
//                                        }
//                                        "rejected" -> {
//                                            ToastUtility.errorToast(
//                                                requireContext(),
//                                                "Your account has been rejected. License picture not clear"
//                                            )
//                                        }
//                                        "pending" -> {
//                                            ToastUtility.errorToast(
//                                                requireContext(),
//                                                "Please wait, your account verification is under process."
//                                            )
//                                        }
//                                    }
//                                }
//                                "admin" -> {
//                                    ToastUtility.successToast(
//                                        requireContext(),
//                                        "Login Admin Successfully."
//                                    )
//                                    gotoAdmin()
//                                }
//                                else -> {
//                                    ToastUtility.successToast(
//                                        requireContext(),
//                                        "Login Client Successfully."
//                                    )
//                                    gotoClientScreen()
//                                }
//                            }
//                        } else {
//
//                            Timber.tag("UserData").d("User is null = " + user)
//
//                        }
//
//                    }
//
//                } else {
//                    ToastUtility.errorToast(requireContext(), "Login Failed.")
//                }
//            }


        }

    }
}





