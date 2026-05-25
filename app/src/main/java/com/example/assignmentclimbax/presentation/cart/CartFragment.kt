package com.example.assignmentclimbax.presentation.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentclimbax.AssignmentApplication
import com.example.assignmentclimbax.R
import com.example.assignmentclimbax.databinding.FragmentCartBinding
import com.example.assignmentclimbax.presentation.common.Resource
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels {
        (requireActivity().application as AssignmentApplication).container.cartViewModelFactory
    }

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        observeViewModel()
        binding.buttonCheckout.setOnClickListener { viewModel.checkout() }
    }

    private fun setupRecycler() {
        cartAdapter = CartAdapter(
            onIncrement = { viewModel.increment(it) },
            onDecrement = { viewModel.decrement(it) }
        )
        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = cartAdapter
    }

    private fun observeViewModel() {
        viewModel.cartUiState.observe(viewLifecycleOwner) { uiState ->
            binding.textTotalPrice.text = String.format(Locale.US, "$%.2f", uiState.totalPrice)
            when (uiState.contentState) {
                is Resource.Empty -> {
                    binding.recyclerCart.visibility = View.GONE
                    binding.layoutCartState.visibility = View.VISIBLE
                    binding.textStateTitle.text = getString(com.example.assignmentclimbax.R.string.empty_cart_title)
                    binding.textStateMessage.text = getString(com.example.assignmentclimbax.R.string.empty_cart_message)
                    binding.buttonCheckout.isEnabled = false
                }
                else -> {
                    binding.layoutCartState.visibility = View.GONE
                    binding.recyclerCart.visibility = View.VISIBLE
                    binding.buttonCheckout.isEnabled = uiState.items.isNotEmpty()
                    cartAdapter.submitList(uiState.items)
                }
            }
        }

        viewModel.checkoutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.buttonCheckout.isEnabled = false
                    binding.progressCheckout.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressCheckout.visibility = View.GONE
                    binding.buttonCheckout.isEnabled = false
                    Snackbar.make(binding.root, R.string.checkout_success, Snackbar.LENGTH_LONG).show()
                    viewModel.clearCheckoutState()
                }
                is Resource.Error -> {
                    binding.progressCheckout.visibility = View.GONE
                    binding.buttonCheckout.isEnabled = true
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.clearCheckoutState()
                }
                null -> Unit
                else -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
