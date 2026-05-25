package com.example.assignmentclimbax

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.example.assignmentclimbax.databinding.ActivityMainBinding
import com.example.assignmentclimbax.util.AuthNavigation.navigateToLoginAsRoot
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
        setupWindowInsets()
        setupToolbarProfile(session)
        setupNavigation()
    }

    private fun setupWindowInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.toolbar_bg)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, windowInsets ->
            val statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBars.top)
            windowInsets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { view, windowInsets ->
            val navBars = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(bottom = navBars.bottom)
            windowInsets
        }
        ViewCompat.requestApplyInsets(binding.mainRoot)
    }

    private fun setupToolbarProfile(session: com.example.assignmentclimbax.domain.model.UserSession?) {
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
        binding.bottomNavigation.menu.apply {
            findItem(R.id.homeFragment)?.title = ""
            findItem(R.id.cartFragment)?.title = ""
        }
    }
}
