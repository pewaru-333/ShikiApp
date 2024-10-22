package org.application.shikiapp

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.application.shikiapp.screens.RootScreen
import org.application.shikiapp.ui.theme.Theme
import org.application.shikiapp.utils.Preferences

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val theme by Preferences.theme.collectAsStateWithLifecycle()
            val dynamicColors by Preferences.dynamicColors.collectAsStateWithLifecycle()

            Theme(theme, dynamicColors) { RootScreen(navController) }
        }
    }
}

