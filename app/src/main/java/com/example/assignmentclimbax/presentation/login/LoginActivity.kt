package com.example.assignmentclimbax.presentation.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmentclimbax.AssignmentApplication
import com.example.assignmentclimbax.databinding.ActivityLoginBinding
import com.example.assignmentclimbax.presentation.common.Resource
import com.example.assignmentclimbax.util.AuthNavigation.navigateToHomeAsRoot
import com.example.assignmentclimbax.util.WindowInsetsHelper
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        (application as AssignmentApplication).container.loginViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = (application as AssignmentApplication).container.authRepository
        if (runBlocking { authRepository.isLoggedIn() }) {
            navigateToHomeAsRoot()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowInsetsHelper.setup(activity = this, topTarget = binding.root)

        binding.buttonLogin.setOnClickListener {
            viewModel.login(
                binding.editUsername.text?.toString().orEmpty(),
                binding.editPassword.text?.toString().orEmpty()
            )
        }

        viewModel.loginState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressLogin.visibility = View.VISIBLE
                    binding.buttonLogin.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressLogin.visibility = View.GONE
                    navigateToHomeAsRoot()
                }
                is Resource.Error -> {
                    binding.progressLogin.visibility = View.GONE
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressLogin.visibility = View.GONE
                    binding.buttonLogin.isEnabled = true
                }
            }
        }
    }
}
