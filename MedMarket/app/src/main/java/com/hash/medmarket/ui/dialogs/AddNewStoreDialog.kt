package com.hash.medmarket.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hash.medmarket.R
import com.hash.medmarket.databinding.DialogInputStoresBinding

class AddNewStoreDialog(
    private val onAddClick: () -> Unit
) : DialogFragment() {
    private lateinit var binding: DialogInputStoresBinding


    private val waitingDialog: WaitingDialog by lazy { WaitingDialog(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogInputStoresBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }



    private fun initClickListeners() {
        binding.dialogCross.setOnClickListener { dismiss() }
    }

}