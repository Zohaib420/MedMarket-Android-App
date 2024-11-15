package com.hash.medmarket.ui.client.fragments.home

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hash.medmarket.R
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.databinding.FragmentClientHomeBinding
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.client.fragments.home.adapter.MedicineAdapter
import com.hash.medmarket.ui.client.fragments.home.adapter.StoreAdapter
import com.hash.medmarket.ui.pharmacists.activities.PharmacistDrawerListener
import com.hash.medmarket.utils.CartManager
import com.hash.medmarket.utils.UserAuthManager
import java.util.Locale

class ClientHomeFragment : Fragment() {

    private lateinit var binding: FragmentClientHomeBinding

    private lateinit var storeAdapter: StoreAdapter

    private lateinit var waitingDialog: WaitingDialog

    private var storeId: String? = null

    private var ownerId: String? = null

    private lateinit var medicineAdapter: MedicineAdapter

    private lateinit var viewModel: ClientHomeViewModel
    private val selectedMedicineIds = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ClientHomeViewModel::class.java]
        initProgressDialog()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ownerName = UserAuthManager.getUserData(requireContext())?.name
        binding.tvTitle.text = "Hello, $ownerName"
        initClickListeners()
        viewModel.allStores()
        viewModel.fetchAllMedicines()
        waitingDialog.show("Getting Data")
        observeStores()
        observeMedicines()
        if (CartManager.getCartData(requireContext()).medicines.isEmpty()) {
            binding.cart.visibility=View.INVISIBLE
        }
        else{
            binding.cart.visibility=View.VISIBLE
        }
        initSearch()
        observeFilteredMedicines()

    }

    private fun initSearch() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                performSearch()
            }
        })
    }

    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        viewModel.searchMedicine(query)
    }


    private fun observeFilteredMedicines() {
        viewModel.filteredMedicines.observe(viewLifecycleOwner, Observer { medicines ->
            if (medicines != null) {
                if (medicines.isNotEmpty()) {
                    binding.emptyDataMedicine.visibility = View.GONE
                    // Pass the selected medicine IDs to the adapter
                    medicineAdapter = MedicineAdapter(medicines, selectedMedicineIds, ::onMedicineClick)
                    binding.rvMedicine.adapter = medicineAdapter
                } else {
                    binding.emptyDataMedicine.visibility = View.VISIBLE
                    medicineAdapter = MedicineAdapter(medicines, selectedMedicineIds, ::onMedicineClick)
                    binding.rvMedicine.adapter = medicineAdapter
                }
            }
        })
    }

    private fun observeMedicines() {
        viewModel.medicines.observe(viewLifecycleOwner, Observer { medicines ->
            waitingDialog.dismiss()
            if (medicines.isNotEmpty()) {
                binding.emptyDataMedicine.visibility = View.GONE
                medicineAdapter = MedicineAdapter(medicines, selectedMedicineIds, ::onMedicineClick)
                binding.rvMedicine.adapter = medicineAdapter
            } else {
                binding.emptyDataMedicine.visibility = View.VISIBLE
            }
        })
    }

    private fun onMedicineClick(medicine: Medicine, medicineId: String, position: Int) {

        ownerId = medicine.ownerId
        val bundle = Bundle().apply {
            putSerializable("selected_medicine", medicine)
            putString("selected_medicine_id", medicineId)
            putString("owner_id", ownerId)
        }
        findNavController().navigate(
            R.id.action_clinetHomeFragment_to_clientMedicineFragment,
            bundle
        )
    }

    private fun observeStores() {

        viewModel.stores.observe(viewLifecycleOwner) { stores ->
            waitingDialog.dismiss()

            if (stores.isNotEmpty()) {
//                binding.emptyData.visibility = View.GONE
                storeAdapter = StoreAdapter(requireContext(), stores) {
                    val bundle = Bundle().apply {
                        putSerializable("owner", it)
                    }
                    findNavController().navigate(
                        R.id.action_clinetHomeFragment_to_clientMedicineFragment, bundle
                    )
                }
                binding.rvStores.adapter = storeAdapter

            } else {
//                binding.emptyData.visibility = View.VISIBLE
            }
        }
    }


    private fun initClickListeners() {

        binding.notificationImage.setOnClickListener {
            findNavController().navigate(R.id.action_clinetHomeFragment_to_notificationFragment2)
        }

        binding.cart.setOnClickListener {

            findNavController().navigate(R.id.action_clinetHomeFragment_to_checkoutFragment)
        }
        binding.viewAll.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("owner_id", ownerId)
            }
            findNavController().navigate(
                R.id.action_clinetHomeFragment_to_clientMedicineFragment,
                bundle
            )
        }

        binding.navImage.setOnClickListener {
            (requireActivity() as PharmacistDrawerListener).openDrawer()
        }

    }

    private fun initProgressDialog() {
        waitingDialog = WaitingDialog(requireContext())
    }



}

