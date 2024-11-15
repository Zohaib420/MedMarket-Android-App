package com.hash.medmarket.ui.pharmacists.fragments.extras.store.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hash.medmarket.R
import com.hash.medmarket.databinding.FragmentMedicineBinding
import com.hash.medmarket.model.medicine.MedicineModel
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.pharmacists.fragments.medicine.allMedicine.adapter.AllMedicineAdapter

class MedicineFragment : Fragment() {

    private lateinit var binding: FragmentMedicineBinding
    private lateinit var adapter: AllMedicineAdapter
    var status: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMedicineBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    val waitingDialog by lazy { WaitingDialog(requireContext()) }
    override fun onDestroyView() {
        waitingDialog.dismiss()
        super.onDestroyView()
    }








}