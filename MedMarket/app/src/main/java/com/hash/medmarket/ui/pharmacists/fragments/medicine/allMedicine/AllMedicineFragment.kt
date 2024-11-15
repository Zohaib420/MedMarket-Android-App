package com.hash.medmarket.ui.pharmacists.fragments.medicine.allMedicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.database.model.Users
import com.hash.medmarket.databinding.FragmentAllMedicineBinding
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.pharmacists.activities.PharmacistDrawerListener
import com.hash.medmarket.ui.pharmacists.fragments.medicine.allMedicine.adapter.AllMedicineAdapter
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager

class AllMedicineFragment : Fragment() {

    private lateinit var binding: FragmentAllMedicineBinding
    private lateinit var adapter: AllMedicineAdapter
    private lateinit var viewModel: MedicineViewModel
    var status: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllMedicineBinding.inflate(layoutInflater, container, false)
        viewModel = MedicineViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeMedicines()

        binding.navImage.setOnClickListener {
            (requireActivity() as PharmacistDrawerListener).openDrawer()
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_allMedicineFragment_to_addNewMedicineFragment,)
        }

    }

    val waitingDialog by lazy { WaitingDialog(requireContext()) }
    override fun onDestroyView() {
        waitingDialog.dismiss()
        super.onDestroyView()
    }

    private fun observeMedicines() {

        binding.tvTitle.text = UserAuthManager.getUserData(requireContext())!!.name.toString()

        waitingDialog.show("Getting Medicine")
        viewModel.getMedicine(UserAuthManager.getUserData(requireContext())!!.userId.toString())
        viewModel.medicines.observe(viewLifecycleOwner) { medicine ->
            waitingDialog.dismiss()
            if (medicine.isNotEmpty()) {
                binding.emptyData.visibility = View.GONE
                adapter = AllMedicineAdapter(medicine) { onOptionClick(it) }
                binding.rvMedicine.adapter = adapter
            } else {
                binding.emptyData.visibility = View.VISIBLE
                adapter = AllMedicineAdapter(medicine) { onOptionClick(it) }
                binding.rvMedicine.adapter = adapter
            }
        }
    }

    private fun onOptionClick(medicine: Medicine) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(requireContext())
            .setTitle("Options")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> onEditClick(medicine)
                    1 -> onDeleteClick(medicine)
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun onDeleteClick(medicine: Medicine) {
        viewModel.deleteMedicine(medicine.medicineId!!)
        viewModel.deleteMedicineResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                ToastUtility.successToast(requireContext(), "Medicine deleted successfully.")
                observeMedicines()
            } else {
                ToastUtility.errorToast(
                    requireContext(),
                    "Failed to delete medicine. Please try again."
                )
            }
        }
    }

    private fun onEditClick(medicine: Medicine) {
        val bundle = Bundle().apply {
            putSerializable("selected_medicine", medicine)
        }
        findNavController().navigate(
            R.id.action_allMedicineFragment_to_addNewMedicineFragment,
            bundle
        )
    }


}