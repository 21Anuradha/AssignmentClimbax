package com.example.assignmentclimbax.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmentclimbax.AssignmentApplication
import com.example.assignmentclimbax.R
import com.example.assignmentclimbax.databinding.FragmentHomeBinding
import com.example.assignmentclimbax.presentation.common.Resource

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        (requireActivity().application as AssignmentApplication).container.homeViewModelFactory
    }

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSearch()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecycler() {
        productAdapter = ProductAdapter(
            onAddToCart = { viewModel.addToCart(it) },
            onIncrement = { viewModel.incrementCart(it) },
            onDecrement = { viewModel.decrementCart(it) }
        )
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProducts.layoutManager = layoutManager
        binding.recyclerProducts.adapter = productAdapter
        binding.recyclerProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val total = productAdapter.itemCount
                if (total > 0 && lastVisible >= total - 3) {
                    viewModel.loadMoreProducts()
                }
            }
        })
    }

    private fun setupSearch() {
        binding.editSearch.addTextChangedListener { text ->
            viewModel.onSearchQueryChanged(text?.toString().orEmpty())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadProducts() }
    }

    private fun observeViewModel() {
        viewModel.productsState.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = false
            when (state) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> showSuccess(state.data)
                is Resource.Error -> showError(state.message)
                is Resource.Empty -> showEmpty()
            }
        }
        viewModel.cartQuantities.observe(viewLifecycleOwner) { quantities ->
            productAdapter.updateCartQuantities(quantities)
        }
        viewModel.addToCartMessage.observe(viewLifecycleOwner) { title ->
            title ?: return@observe
            Snackbar.make(
                binding.root,
                getString(R.string.added_to_cart, title),
                Snackbar.LENGTH_SHORT
            ).show()
            viewModel.clearAddToCartMessage()
        }
        viewModel.isLoadingMore.observe(viewLifecycleOwner) { loading ->
            binding.progressLoadMore.visibility = if (loading == true) View.VISIBLE else View.GONE
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutState.visibility = View.GONE
        binding.recyclerProducts.visibility = View.GONE
        binding.swipeRefresh.isEnabled = false
    }

    private fun showSuccess(products: List<com.example.assignmentclimbax.domain.model.Product>) {
        binding.progressBar.visibility = View.GONE
        binding.layoutState.visibility = View.GONE
        binding.recyclerProducts.visibility = View.VISIBLE
        binding.swipeRefresh.isEnabled = true
        productAdapter.submitList(products)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerProducts.visibility = View.GONE
        binding.swipeRefresh.isEnabled = true
        binding.layoutState.visibility = View.VISIBLE
        binding.imageState.setImageResource(android.R.drawable.ic_dialog_alert)
        binding.textStateTitle.text = getString(R.string.error_title)
        binding.textStateMessage.text = message
        binding.buttonRetry.visibility = View.VISIBLE
        binding.buttonRetry.setOnClickListener { viewModel.loadProducts() }
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerProducts.visibility = View.GONE
        binding.swipeRefresh.isEnabled = true
        binding.layoutState.visibility = View.VISIBLE
        binding.imageState.setImageResource(android.R.drawable.ic_menu_search)
        binding.textStateTitle.text = getString(R.string.empty_title)
        binding.textStateMessage.text = getString(R.string.empty_products)
        binding.buttonRetry.visibility = View.VISIBLE
        binding.buttonRetry.setOnClickListener { viewModel.loadProducts() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
