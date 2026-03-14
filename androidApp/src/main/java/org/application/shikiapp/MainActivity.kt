package org.application.shikiapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import org.application.shikiapp.shared.App
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                content = { App() },
                values = arrayOf(
                    LocalActivity provides this,
                    LocalActivityResultRegistryOwner provides this
                )
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.data != null) {
            ExternalUriHandler.onNewUri(intent.data.toString())
        }
    }
}