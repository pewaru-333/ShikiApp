package org.application.shikiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.application.shikiapp.ui.theme.Theme
import org.application.shikiapp.utils.navigation.BottomNavigationBar
import org.application.shikiapp.utils.navigation.Navigation
import org.application.shikiapp.utils.toBottomBarItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navigator = rememberNavController()
            val backStack by navigator.currentBackStackEntryAsState()

            Theme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(backStack, navigator::toBottomBarItem) },
                    content = { Navigation(navigator, Modifier.padding(it)) }
                )
            }
        }
    }
}