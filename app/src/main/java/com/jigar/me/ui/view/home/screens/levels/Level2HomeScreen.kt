package com.jigar.me.ui.view.home.screens.levels

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground

@Composable
fun Level2HomeScreen(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(title = "Add & Subtract", onBackClick = onBackClick)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "➕  Level 2 — Coming Soon!",
                    style = MaterialTheme.typography.headlineMedium.scaled(),
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
        }
    }
}
