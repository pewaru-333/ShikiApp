package org.application.shikiapp.utils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
interface NavigationBarVisibility {
    val isVisible: Boolean

    fun show()
    fun hide()
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
}

@Composable
fun rememberNavigationBarVisibility(): NavigationBarVisibility {
    val state = remember { InitialNavigationBarVisibility() }

    return state
}