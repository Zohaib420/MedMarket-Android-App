package com.hash.medmarket.ui.client.fragments.home.checkout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.hash.medmarket.database.model.Medicine
import com.hash.medmarket.databinding.FragmentCheckoutBinding
import com.hash.medmarket.notifications.NotificationSender
import com.hash.medmarket.ui.auth.dialog.WaitingDialog
import com.hash.medmarket.ui.client.activities.MainClientActivity
import com.hash.medmarket.ui.client.fragments.home.checkout.adapter.CheckoutAdapter
import com.hash.medmarket.ui.client.fragments.home.checkout.adapter.SelectedItemAdapter
import com.hash.medmarket.utils.Cart
import com.hash.medmarket.utils.CartManager
import com.hash.medmarket.utils.ToastUtility
import com.hash.medmarket.utils.UserAuthManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutFragment : Fragment(), CheckoutAdapter.TotalPriceUpdateListener {

    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var selectedAdapter: SelectedItemAdapter
    private lateinit var adapter: CheckoutAdapter
    private lateinit var waitingDialog: WaitingDialog
    private var ownerId: String? = null
    private val quantityList = ArrayList<Pair<String, Pair<Medicine, Boolean>>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ownerId = arguments?.getString("owner_id") as? String

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.order.btnOrderNow.setOnClickListener {

            quantityList.forEach {
                println("Order: ${it.first}  ${it.second.first.name}  ${it.second.second}")
            }

            placeOrder()
        }
        binding.btnClearCart.setOnClickListener { clearCart() }

        // Create an instance of SelectedItemAdapter
        selectedAdapter = SelectedItemAdapter(mutableListOf())
        binding.order.rvSelectedMedicines.adapter = selectedAdapter

        // Pass selectedAdapter along with other parameters when creating CheckoutAdapter
        val cart = CartManager.getCartData(requireContext())
        adapter = CheckoutAdapter(cart.medicines, ::onCheckedClick, this, selectedAdapter)
        binding.rvCheckout.adapter = adapter
        waitingDialog = WaitingDialog(requireContext())
    }


    private fun clearCart() {
        CartManager.clearCartData(requireContext())
        val cart = Cart.getInstance()
        cart.medicines.clear()
        CartManager.saveCartData(requireContext(), cart)
        adapter.clearItems()
        binding.order.root.visibility = View.GONE
        ToastUtility.successToast(requireContext(), "Cart cleared successfully!")
        startActivity(Intent(requireContext(), MainClientActivity::class.java))
    }

    private fun placeOrder() {
        if (selectedAdapter.itemCount > 0) {
            waitingDialog.show("Placing Order")
            val formattedDate =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            viewModel.order.userId = UserAuthManager.getUserData(requireContext())!!.userId
            viewModel.order.name = UserAuthManager.getUserData(requireContext())!!.name
            viewModel.order.phone = UserAuthManager.getUserData(requireContext())!!.phone
            viewModel.order.timestamp = formattedDate
            viewModel.order.status = "pending"
            viewModel.order.pharmacistId =
                ownerId ?: CartManager.getCartData(requireContext()).medicines.first().ownerId
            viewModel.order.medicines = selectedAdapter.getSelectedMedicines()
            viewModel.saveOrder { success ->
                waitingDialog.dismiss()
                if (success) {
                    CartManager.clearCartData(requireContext())
                    FirebaseFirestore.getInstance().collection("Users")
                        .document(ownerId.toString())
                        .get().addOnSuccessListener {
                            NotificationSender.sendNotification(
                                "Order Received",
                                "You have received a new order",
                                requireContext(), it.get("token").toString()
                            )
                        }
                    ToastUtility.successToast(requireContext(), "Order placed successfully!")
                    startActivity(Intent(requireContext(), MainClientActivity::class.java))
                } else {
                    ToastUtility.errorToast(requireContext(), "Failed to place the order!")
                }
            }
        } else {
            ToastUtility.errorToast(requireContext(), "Please select medicines to place the order.")
        }
    }

    private fun onCheckedClick(
        cart: Medicine,
        isChecked: Boolean,
        quantity: String,
        isAdd: Boolean
    ) {
        if (isChecked) {
            if (selectedAdapter.getSelectedMedicines().contains(cart)) {
                selectedAdapter.updateMedicine(quantity, cart, isAdd)
            } else {
                selectedAdapter.addMedicine(cart)
            }
            updateQuantityList(cart, quantity, isChecked)
        } else {
            selectedAdapter.removeMedicine(cart)
            updateQuantityList(cart, quantity, isChecked)
        }

        // Calculate total price and update UI
        val totalPrice = calculateTotalPrice()
        onTotalPriceUpdate(totalPrice)
    }

    private fun updateQuantityList(cart: Medicine, quantity: String, isChecked: Boolean) {
        val pairToAdd = Pair(quantity, Pair(cart, isChecked))
        val pairToRemove = Pair(quantity, Pair(cart, true))
        if (isChecked) {
            if (!quantityList.contains(pairToAdd)) {
                quantityList.add(pairToAdd)
            }
        } else {
            quantityList.remove(pairToRemove)

        }
    }


    private fun calculateTotalPrice(): Int {
        var totalPrice = 0
        for ((quantity, pair) in quantityList) {
            val (medicine, isChecked) = pair
            if (isChecked) {

                val itemQuantity = quantity.toIntOrNull() ?: 0

                val itemPrice = medicine.price?.toIntOrNull() ?: 0
                totalPrice += itemPrice * itemQuantity
            }
        }
        return totalPrice
    }




    override fun onTotalPriceUpdate(totalPrice: Int) {
        binding.order.tvCartCount.text = totalPrice.toString()
        binding.order.root.visibility = if (totalPrice > 0) View.VISIBLE else View.GONE
    }
}
