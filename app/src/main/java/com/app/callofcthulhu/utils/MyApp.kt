package com.app.callofcthulhu.utils

import android.app.Application
import com.app.callofcthulhu.viewModel.SharedViewModel

class MyApp : Application() {
    companion object {
        lateinit var sharedViewModel: SharedViewModel
    }

    override fun onCreate() {
        super.onCreate()
        sharedViewModel = SharedViewModel()
    }
}
