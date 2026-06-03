package com.example.application

import android.app.Application
import com.example.application.ui.bdd.AppContainer
import com.example.application.ui.bdd.AppDataContainer

class TaskApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}