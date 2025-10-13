package org.application.shikiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.ui.theme.Theme
import org.application.shikiapp.utils.extensions.safeDeepLink
import org.application.shikiapp.utils.navigation.LocalBarVisibility
import org.application.shikiapp.utils.navigation.Navigation
import org.application.shikiapp.utils.navigation.rememberNavigationBarVisibility

class MainActivity : ComponentActivity() {

    private lateinit var navigator: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            navigator = rememberNavController()
            val barVisibility = rememberNavigationBarVisibility()

            CompositionLocalProvider(LocalBarVisibility provides barVisibility) {
                Theme {
                    Navigation(navigator)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        navigator.safeDeepLink(intent)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(Preferences.changeLanguage(newBase))
    }
}