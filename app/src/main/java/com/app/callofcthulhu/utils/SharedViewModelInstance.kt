package com.app.callofcthulhu.utils

import com.app.callofcthulhu.viewModel.SharedViewModel

object SharedViewModelInstance {
    private var _instance: SharedViewModel? = null

    val instance: SharedViewModel
        get() {
            if (_instance == null) {
                _instance = SharedViewModel()
            }
            return _instance!!
        }

    fun clearInstance() {
        _instance = null
    }
}
