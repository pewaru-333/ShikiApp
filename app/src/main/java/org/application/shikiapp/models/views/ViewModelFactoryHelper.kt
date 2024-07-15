package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <viewModel : ViewModel> factory(initializer: () -> viewModel): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = initializer() as T
    }