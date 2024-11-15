package com.hash.medmarket.ui.auth.fragments.forgot

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.hash.medmarket.databinding.FragmentForgotPasswordBinding
import com.hash.medmarket.ui.auth.dialog.ProgressDialog
import com.hash.medmarket.utils.ToastUtility


class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressDialog()
        initClickListeners()
    }


    private fun initClickListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnSend.setOnClickListener {
            val email = binding.etEmailAddress.text.toString()
            if (email.isBlank() || email.isEmpty()) {
                ToastUtility.errorToast(requireActivity(), "Please Input Something")
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ToastUtility.errorToast(requireActivity(), "Please enter a valid email address")
                return@setOnClickListener
            } else {
                progressDialog.showDialogue()
                sendPasswordResetEmail(email)
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {

        progressDialog.showDialogue()

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    progressDialog.hideDialogue()
                    ToastUtility.successToast(requireActivity(), "Password reset email sent successfully.")
                } else {
                    progressDialog.hideDialogue()
                    ToastUtility.errorToast(requireActivity(), "Error sending password reset email: ${task.exception?.message}")
                }
            }
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
    }


}