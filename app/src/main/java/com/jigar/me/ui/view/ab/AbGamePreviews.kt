package com.jigar.me.ui.view.ab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.theme.ButtonType

/** The shared Game Zone result card over a dimmed screen (debug only): `ab_screen=gameresult` = 3 stars, `gameresult1` = 1 star. */
@Composable
fun AbGameResultPreview(stars: Int) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AbBackground(AbWorld.Sunset, Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
        GameResultCard(
            title = if (stars >= 2) "Great Job!" else "Good try!", emoji = "⚖️", accent = ButtonType.ORANGE, stars = stars, score = "Final score: 12",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip("🎯", "10", Color(0xFF2E7D32))
                    GameStatChip("❌", "2", Color(0xFFD32F2F))
                    GameStatChip("🏆", "14", Color(0xFFB45309))
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GameButton(text = "Play Again", icon = Icons.Filled.PlayArrow, type = ButtonType.GREEN, onClick = {})
                    GameHomeButton(onClick = {})
                }
            },
        )
    }
}

/** All the Game Zone friends side by side (debug only): `ab_screen=characters` = waiting, `characters2` = cheering. */
@Composable
fun AbCharactersPreview(cheer: Boolean) {
    var t by androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val start = androidx.compose.runtime.withFrameNanos { it }
        while (true) { t = (androidx.compose.runtime.withFrameNanos { it } - start) / 1_000_000_000f }
    }
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
        val all = com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter.entries
        androidx.compose.foundation.layout.Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { all.take(6).forEach { Friend(it, t, cheer) } }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { all.drop(6).forEach { Friend(it, t, cheer) } }
        }
    }
}

@Composable
private fun Friend(c: com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter, t: Float, cheer: Boolean) {
    androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
        com.jigar.me.ui.view.home.screens.math_game_zone.common.CartoonFriend(c, 150.dp, t, cheer)
        androidx.compose.material3.Text(c.charName, color = Color.Black)
    }
}
