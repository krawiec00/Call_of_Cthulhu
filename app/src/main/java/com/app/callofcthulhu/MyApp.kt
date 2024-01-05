package com.app.callofcthulhu

import android.app.Application

class MyApp : Application() {
    companion object {
        lateinit var sharedViewModel: SharedViewModel
    }

    override fun onCreate() {
        super.onCreate()
        sharedViewModel = SharedViewModel()
    }
}
