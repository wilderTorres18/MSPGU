package com.sunarp.mspgu.viewmodel

import com.sunarp.mspgu.viewmodel.NetworkLocationViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NetworkLocationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NetworkLocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NetworkLocationViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
