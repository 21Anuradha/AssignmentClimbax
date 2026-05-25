package com.example.assignmentclimbax.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.assignmentclimbax.domain.model.Product
import com.example.assignmentclimbax.domain.usecase.AddToCartUseCase
import com.example.assignmentclimbax.domain.usecase.DecrementCartQuantityUseCase
import com.example.assignmentclimbax.domain.usecase.GetProductsUseCase
import com.example.assignmentclimbax.domain.usecase.IncrementCartQuantityUseCase
import com.example.assignmentclimbax.domain.usecase.ObserveCartQuantitiesUseCase
import com.example.assignmentclimbax.domain.usecase.SearchProductsUseCase
import com.example.assignmentclimbax.presentation.common.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    observeCartQuantitiesUseCase: ObserveCartQuantitiesUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val incrementCartQuantityUseCase: IncrementCartQuantityUseCase,
    private val decrementCartQuantityUseCase: DecrementCartQuantityUseCase
) : ViewModel() {

    private val _productsState = MutableLiveData<Resource<List<Product>>>(Resource.Loading)
    val productsState: LiveData<Resource<List<Product>>> = _productsState

    private val _isLoadingMore = MutableLiveData(false)
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _hasMorePages = MutableLiveData(true)
    val hasMorePages: LiveData<Boolean> = _hasMorePages

    val cartQuantities: LiveData<Map<Int, Int>> =
        observeCartQuantitiesUseCase().asLiveData()

    private val searchQueryFlow = MutableStateFlow("")
    private var loadJob: Job? = null
    private var isSearchMode = false
    private var currentSkip = 0
    private val accumulatedProducts = mutableListOf<Product>()

    init {
        loadProducts(refresh = true)
        viewModelScope.launch {
            searchQueryFlow
                .debounce(400)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { query -> performSearch(query) }
        }
    }

    fun loadProducts(refresh: Boolean = false) {
        if (refresh) {
            currentSkip = 0
            accumulatedProducts.clear()
            _hasMorePages.value = true
        } else if (_isLoadingMore.value == true || _hasMorePages.value != true) {
            return
        }
        isSearchMode = false
        loadJob?.cancel()
        if (refresh) {
            _productsState.value = Resource.Loading
        } else {
            _isLoadingMore.value = true
        }
        loadJob = viewModelScope.launch {
            getProductsUseCase(PAGE_SIZE, currentSkip)
                .onSuccess { page ->
                    accumulatedProducts.addAll(page.products)
                    currentSkip += page.products.size
                    _hasMorePages.postValue(page.hasMore)
                    _isLoadingMore.postValue(false)
                    _productsState.postValue(
                        if (accumulatedProducts.isEmpty()) Resource.Empty
                        else Resource.Success(accumulatedProducts.toList())
                    )
                }
                .onFailure {
                    _isLoadingMore.postValue(false)
                    if (accumulatedProducts.isEmpty()) {
                        _productsState.postValue(Resource.Error(it.message ?: "Failed to load products"))
                    } else {
                        _productsState.postValue(Resource.Success(accumulatedProducts.toList()))
                    }
                }
        }
    }

    fun loadMoreProducts() {
        if (!isSearchMode) {
            loadProducts(refresh = false)
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query
        if (query.isBlank()) {
            loadProducts(refresh = true)
        }
    }

    private fun performSearch(query: String) {
        isSearchMode = true
        _hasMorePages.value = false
        loadJob?.cancel()
        _productsState.value = Resource.Loading
        loadJob = viewModelScope.launch {
            searchProductsUseCase(query)
                .onSuccess { products ->
                    _productsState.postValue(
                        if (products.isEmpty()) Resource.Empty else Resource.Success(products)
                    )
                }
                .onFailure {
                    _productsState.postValue(Resource.Error(it.message ?: "Search failed"))
                }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch { addToCartUseCase(product) }
    }

    fun incrementCart(productId: Int) {
        viewModelScope.launch { incrementCartQuantityUseCase(productId) }
    }

    fun decrementCart(productId: Int) {
        viewModelScope.launch { decrementCartQuantityUseCase(productId) }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}

class HomeViewModelFactory(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val observeCartQuantitiesUseCase: ObserveCartQuantitiesUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val incrementCartQuantityUseCase: IncrementCartQuantityUseCase,
    private val decrementCartQuantityUseCase: DecrementCartQuantityUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                getProductsUseCase,
                searchProductsUseCase,
                observeCartQuantitiesUseCase,
                addToCartUseCase,
                incrementCartQuantityUseCase,
                decrementCartQuantityUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
