package com.sunarp.mspgu.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LocationPermissionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationPermissionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationPermissionViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
