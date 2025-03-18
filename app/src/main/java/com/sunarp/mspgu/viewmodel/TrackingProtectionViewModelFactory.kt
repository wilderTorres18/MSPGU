package com.sunarp.mspgu.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TrackingProtectionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackingProtectionViewModel::class.java)) {
            return TrackingProtectionViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
