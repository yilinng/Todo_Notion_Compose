package com.example.todonotioncompose

import android.app.Application
import com.example.todonotioncompose.data.AppContainer
import com.example.todonotioncompose.data.DefaultAppContainer

class TodoApplication : Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}