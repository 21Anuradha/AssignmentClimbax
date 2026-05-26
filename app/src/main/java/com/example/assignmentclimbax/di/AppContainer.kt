package com.example.assignmentclimbax.di

import android.content.Context
import androidx.room.Room
import com.example.assignmentclimbax.data.local.db.AppDatabase
import com.example.assignmentclimbax.data.prefs.SessionDataStore
import com.example.assignmentclimbax.data.remote.api.DummyJsonApi
import com.example.assignmentclimbax.data.repository.AuthRepositoryImpl
import com.example.assignmentclimbax.data.repository.CartRepositoryImpl
import com.example.assignmentclimbax.data.repository.ProductRepositoryImpl
import com.example.assignmentclimbax.domain.repository.AuthRepository
import com.example.assignmentclimbax.domain.repository.CartRepository
import com.example.assignmentclimbax.domain.repository.ProductRepository
import com.example.assignmentclimbax.domain.usecase.AddToCartUseCase
import com.example.assignmentclimbax.domain.usecase.CheckoutUseCase
import com.example.assignmentclimbax.domain.usecase.DecrementCartQuantityUseCase
import com.example.assignmentclimbax.domain.usecase.GetProductsUseCase
import com.example.assignmentclimbax.domain.usecase.IncrementCartQuantityUseCase
import com.example.assignmentclimbax.domain.usecase.LoginUseCase
import com.example.assignmentclimbax.domain.usecase.ObserveCartItemsUseCase
import com.example.assignmentclimbax.domain.usecase.ObserveCartQuantitiesUseCase
import com.example.assignmentclimbax.domain.usecase.SearchProductsUseCase
import com.example.assignmentclimbax.presentation.cart.CartViewModelFactory
import com.example.assignmentclimbax.presentation.home.HomeViewModelFactory
import com.example.assignmentclimbax.presentation.login.LoginViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {
    companion object {
        const val BASE_URL = "https://dummyjson.com/"
    }
    private val appContext = context.applicationContext
    private val sessionDataStore: SessionDataStore = SessionDataStore(appContext)
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: DummyJsonApi = retrofit.create(DummyJsonApi::class.java)

    private val database: AppDatabase = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        "flipzon_db"
    ).build()

    private val cartDao = database.cartDao()
    private val productRepository: ProductRepository = ProductRepositoryImpl(api)
    private val cartRepository: CartRepository = CartRepositoryImpl(cartDao, api)
    val authRepository: AuthRepository = AuthRepositoryImpl(api, sessionDataStore, cartDao)
    private val getProductsUseCase = GetProductsUseCase(productRepository)
    private val searchProductsUseCase = SearchProductsUseCase(productRepository)
    private val observeCartQuantitiesUseCase = ObserveCartQuantitiesUseCase(cartRepository)
    private val addToCartUseCase = AddToCartUseCase(cartRepository)
    private val incrementCartQuantityUseCase = IncrementCartQuantityUseCase(cartRepository)
    private val observeCartItemsUseCase = ObserveCartItemsUseCase(cartRepository)
    private val checkoutUseCase = CheckoutUseCase(cartRepository)
    private val loginUseCase = LoginUseCase(authRepository)
    val loginViewModelFactory = LoginViewModelFactory(loginUseCase)
    private val decrementCartQuantityUseCase = DecrementCartQuantityUseCase(cartRepository)

    val homeViewModelFactory = HomeViewModelFactory(
        getProductsUseCase,
        searchProductsUseCase,
        observeCartQuantitiesUseCase,
        addToCartUseCase,
        incrementCartQuantityUseCase,
        decrementCartQuantityUseCase
    )
    val cartViewModelFactory = CartViewModelFactory(
        observeCartItemsUseCase,
        incrementCartQuantityUseCase,
        decrementCartQuantityUseCase,
        checkoutUseCase,
        authRepository
    )
}
