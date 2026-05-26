package com.example.assignmentclimbax

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.example.assignmentclimbax.databinding.ActivityMainBinding
import com.example.assignmentclimbax.domain.model.UserSession
import com.example.assignmentclimbax.util.AuthNavigation.navigateToLoginAsRoot
import com.example.assignmentclimbax.util.WindowInsetsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = (application as AssignmentApplication).container.authRepository

        if (!runBlocking { authRepository.isLoggedIn() }) {
            navigateToLoginAsRoot()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = runBlocking { authRepository.getSession() }
        window.statusBarColor = ContextCompat.getColor(this, R.color.toolbar_bg)
        WindowInsetsHelper.setup(
            activity = this,
            topTarget = binding.toolbar,
            bottomTarget = binding.bottomNavigation
        )
        ViewCompat.requestApplyInsets(binding.mainRoot)
        setupToolbarProfile(session)
        setupNavigation()
    }

    private fun setupToolbarProfile(session: UserSession?) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        session?.let { user ->
            binding.toolbarProfile.textFullName.text = user.fullName
            binding.toolbarProfile.textEmail.text = user.email
            binding.toolbarProfile.imageProfile.load(user.image) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher)
                error(R.mipmap.ic_launcher)
            }
        }

        binding.buttonLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        val authRepository = (application as AssignmentApplication).container.authRepository
        runBlocking {
            withContext(Dispatchers.IO) {
                authRepository.logout()
            }
        }
        navigateToLoginAsRoot()
    }

    private fun setupNavigation() {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHost.navController)
    }
}
