package com.hash.medmarket.ui.pharmacists.fragments.support

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hash.medmarket.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {
    private lateinit var binding: FragmentHelpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        updateUi()
    }

    private fun updateUi() {
        binding.let {
            it.detail.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    "Welcome to our app! This is a help text for Android N and above. You can find useful information about our app features and how to use them. If you have any questions or need assistance, please don't hesitate to contact us.",
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                "Welcome to our app! This is a help text for Android versions below N. You can find useful information about our app features and how to use them. If you have any questions or need assistance, please don't hesitate to contact us."
            }
        }
    }


//    private fun updateUi(data: TermsConditionResponse.Data?) {
//
//        binding.let {
//            it.detail.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Html.fromHtml(data?.content, Html.FROM_HTML_MODE_COMPACT)
//            } else {
//                Html.fromHtml(data?.content)
//            }
//            it.tvTitle.text = data?.title
//        }
//    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }
    }
}