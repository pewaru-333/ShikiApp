package org.application.shikiapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.application.shikiapp.ui.theme.Theme
import org.application.shikiapp.utils.extensions.rememberNavigationBarVisibility
import org.application.shikiapp.utils.extensions.safeDeepLink
import org.application.shikiapp.utils.extensions.toBottomBarItem
import org.application.shikiapp.utils.navigation.BottomNavigationBar
import org.application.shikiapp.utils.navigation.Navigation

class MainActivity : ComponentActivity() {

    private lateinit var navigator: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            navigator = rememberNavController()
            val backStack by navigator.currentBackStackEntryAsState()
            val barVisibility = rememberNavigationBarVisibility()

            Theme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(backStack, barVisibility.isVisible, navigator::toBottomBarItem) },
                    content = { Navigation(navigator, barVisibility, Modifier.padding(it)) }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        navigator.safeDeepLink(intent)
    }
}