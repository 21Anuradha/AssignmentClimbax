package com.example.assignmentclimbax

import android.app.Application
import com.example.assignmentclimbax.di.AppContainer

class AssignmentApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
