package com.example.assignmentclimbax.util

import android.app.Activity
import android.content.Intent
import com.example.assignmentclimbax.MainActivity
import com.example.assignmentclimbax.presentation.login.LoginActivity

object AuthNavigation {

    private const val ROOT_FLAGS =
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

    fun Activity.navigateToHomeAsRoot() {
        startActivity(Intent(this, MainActivity::class.java).apply { flags = ROOT_FLAGS })
        finish()
    }

    fun Activity.navigateToLoginAsRoot() {
        startActivity(Intent(this, LoginActivity::class.java).apply { flags = ROOT_FLAGS })
        finish()
    }
}
