package com.example.application.ui

import android.app.Application
import android.content.Context
import com.example.application.ui.bdd.AppContainer
import com.example.application.ui.bdd.AppDataContainer
import org.osmdroid.config.Configuration

class TaskApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName
        
        container = AppDataContainer(this)
    }
}
