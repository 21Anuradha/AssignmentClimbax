package com.example.assignmentclimbax.util

import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding

object WindowInsetsHelper {

    fun setup(
        activity: ComponentActivity,
        topTarget: View,
        bottomTarget: View? = null,
        lightStatusBar: Boolean = false,
        applyTopInsetPadding: Boolean = true,
        useEdgeToEdge: Boolean = true
    ) {
        if (useEdgeToEdge) {
            activity.enableEdgeToEdge()
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        } else {
            WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        }

        WindowInsetsControllerCompat(activity.window, topTarget).apply {
            isAppearanceLightStatusBars = lightStatusBar
        }

        if (!useEdgeToEdge) {
            return
        }

        if (bottomTarget == null || bottomTarget === topTarget) {
            ViewCompat.setOnApplyWindowInsetsListener(topTarget) { view, windowInsets ->
                val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(
                    left = bars.left,
                    top = if (applyTopInsetPadding) bars.top else 0,
                    right = bars.right,
                    bottom = bars.bottom
                )
                windowInsets
            }
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(topTarget) { view, windowInsets ->
                val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(
                    left = bars.left,
                    top = if (applyTopInsetPadding) bars.top else 0,
                    right = bars.right
                )
                windowInsets
            }
            ViewCompat.setOnApplyWindowInsetsListener(bottomTarget) { view, windowInsets ->
                val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(bottom = bars.bottom)
                windowInsets
            }
        }
    }
}
