package org.application.shikiapp.shared.utils.ui

import org.jetbrains.compose.resources.StringResource

interface IToast {
    fun onShow(resource: StringResource)
    fun onShow(text: String)
}