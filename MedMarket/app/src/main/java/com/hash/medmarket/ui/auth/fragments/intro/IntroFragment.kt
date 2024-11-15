package com.hash.medmarket.ui.auth.fragments.intro

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hash.medmarket.R
import com.hash.medmarket.databinding.FragmentIntroBinding
import com.hash.medmarket.ui.auth.dialog.ProgressDialog

class IntroFragment : Fragment() {

    private lateinit var binding: FragmentIntroBinding

    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressDialog()

        initClickListeners()


    }


    private fun initClickListeners() {

        binding.btnPharmacistSignUp.setOnClickListener {

            findNavController().navigate(R.id.action_introFragment_to_pharmacistSignUpFragment)

        }

        binding.btnClientSignUp.setOnClickListener {

            findNavController().navigate(R.id.action_introFragment_to_clientSignupFragment)

        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }


    }

    private fun initProgressDialog() {

        progressDialog = ProgressDialog(requireContext())

    }



}