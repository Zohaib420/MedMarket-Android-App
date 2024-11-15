package com.hash.medmarket.ui.auth.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.hash.medmarket.databinding.DialogWaitingBinding
import java.util.*

class WaitingDialog(context: Context) : Dialog(context) {
    private var _binding: DialogWaitingBinding? = null
    private val binding get() = _binding!!
    private var status: String? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        _binding = DialogWaitingBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        Objects.requireNonNull(window)?.setBackgroundDrawable(
            ColorDrawable(
                context.resources.getColor(
                    android.R.color.transparent,
                    null,
                )
            )
        )
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        init()
    }

    private fun init() {
        if (status != null) {
            binding.tvStatusLoading.text = status
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (_binding != null) {
            binding.tvStatusLoading.text = status
        }
    }


    fun show(status: String?) {
        this.status = status
        if (_binding != null) {
            binding.tvStatusLoading.text = status
        }
        show()
    }

    fun setStatusTextView(status: String?) {
        binding.tvStatusLoading.text = status
    }


}