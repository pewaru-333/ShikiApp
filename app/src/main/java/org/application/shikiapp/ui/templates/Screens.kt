package org.application.shikiapp.ui.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) =
    Box(modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator()
    }

@Composable
fun ErrorScreen(retry: () -> Unit = {}) =
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("Ошибка загрузки!")
        Button(retry) { Text("Повторить") }
    }