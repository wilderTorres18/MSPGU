package com.sunarp.mspgu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Feedback(val rating: Int, val comment: String)

class FeedbackViewModel : ViewModel() {

    private val _feedbackList = MutableStateFlow<List<Feedback>>(emptyList())
    val feedbackList = _feedbackList.asStateFlow()

    private val _feedbackSent = MutableStateFlow(false)
    val feedbackSent = _feedbackSent.asStateFlow()

    fun submitFeedback(rating: Int, comment: String) {
        viewModelScope.launch {
            val newFeedback = Feedback(rating, comment)
            _feedbackList.value = _feedbackList.value + newFeedback
            _feedbackSent.value = true
        }
    }

    fun resetFeedback() {
        _feedbackSent.value = false
    }
}
