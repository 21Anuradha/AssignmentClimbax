package com.example.assignmentclimbax.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.assignmentclimbax.domain.model.CartItem
import com.example.assignmentclimbax.domain.repository.AuthRepository
import com.example.assignmentclimbax.domain.usecase.CheckoutUseCase
import com.example.assignmentclimbax.domain.usecase.DecrementCartQuantityUseCase
import com.example.assignmentclimbax.domain.usecase.IncrementCartQuantityUseCase
import com.example.assignmentclimbax.domain.usecase.ObserveCartItemsUseCase
import com.example.assignmentclimbax.presentation.common.Resource
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CartViewModel(
    observeCartItemsUseCase: ObserveCartItemsUseCase,
    private val incrementCartQuantityUseCase: IncrementCartQuantityUseCase,
    private val decrementCartQuantityUseCase: DecrementCartQuantityUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    val cartUiState: LiveData<CartUiState> = observeCartItemsUseCase()
        .map { items -> buildUiState(items) }
        .asLiveData(viewModelScope.coroutineContext)

    private val _checkoutState = MutableLiveData<Resource<Unit>?>()
    val checkoutState: LiveData<Resource<Unit>?> = _checkoutState

    private fun buildUiState(items: List<CartItem>): CartUiState {
        val total = items.sumOf { it.lineTotal }
        val contentState = if (items.isEmpty()) Resource.Empty else Resource.Success(Unit)
        return CartUiState(items, total, contentState)
    }

    fun increment(productId: Int) {
        viewModelScope.launch { incrementCartQuantityUseCase(productId) }
    }

    fun decrement(productId: Int) {
        viewModelScope.launch { decrementCartQuantityUseCase(productId) }
    }

    fun checkout() {
        _checkoutState.value = Resource.Loading
        viewModelScope.launch {
            val userId = authRepository.getLoggedInUserId()
            if (userId == null) {
                _checkoutState.postValue(Resource.Error("User not logged in"))
                return@launch
            }
            checkoutUseCase(userId)
                .onSuccess { _checkoutState.postValue(Resource.Success(Unit)) }
                .onFailure {
                    _checkoutState.postValue(Resource.Error(it.message ?: "Checkout failed"))
                }
        }
    }

    fun clearCheckoutState() {
        _checkoutState.value = null
    }
}

data class CartUiState(
    val items: List<CartItem>,
    val totalPrice: Double,
    val contentState: Resource<Unit>
)

class CartViewModelFactory(
    private val observeCartItemsUseCase: ObserveCartItemsUseCase,
    private val incrementCartQuantityUseCase: IncrementCartQuantityUseCase,
    private val decrementCartQuantityUseCase: DecrementCartQuantityUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(
                observeCartItemsUseCase,
                incrementCartQuantityUseCase,
                decrementCartQuantityUseCase,
                checkoutUseCase,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
