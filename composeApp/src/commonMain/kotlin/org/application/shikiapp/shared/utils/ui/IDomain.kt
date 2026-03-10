package org.application.shikiapp.shared.utils.ui

interface IDomain {
    val isVerified: Boolean
    fun onSettingsLaunch()
}