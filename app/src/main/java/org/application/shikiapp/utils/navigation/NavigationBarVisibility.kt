package org.application.shikiapp.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
interface NavigationBarVisibility {
    val isVisible: Boolean

    fun show()
    fun hide()
    fun toggle(hide: Boolean)
}

private class InitialNavigationBarVisibility : NavigationBarVisibility {
    private val _isVisible = mutableStateOf(true)
    override val isVisible by _isVisible

    override fun show() {
        _isVisible.value = true
    }

    override fun hide() {
        _isVisible.value = false
    }

    override fun toggle(hide: Boolean) {
        _isVisible.value = !hide
    }
}

val LocalBarVisibility = compositionLocalOf<NavigationBarVisibility> {
    InitialNavigationBarVisibility()
}

@Composable
fun rememberNavigationBarVisibility(): NavigationBarVisibility {
    return remember(::InitialNavigationBarVisibility)
}