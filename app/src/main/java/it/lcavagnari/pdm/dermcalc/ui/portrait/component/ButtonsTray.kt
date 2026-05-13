package it.lcavagnari.pdm.dermcalc.ui.portrait.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalToggleDarkTheme

@Composable
fun TopTrayButtons(
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    showDebug: Boolean = false,
    onDebugClick: () -> Unit = {},
    onToggleTheme: () -> Unit,
    onLangClick: () -> Unit) {
    val toggleDarkTheme = LocalToggleDarkTheme.current

    // TODO: Remove this button before release, you dumb cu-
    if (showDebug) {
        Box(
            modifier = modifier
                .size(40.dp)
                .padding(end = 15.dp)
                .clickable(onClick = onDebugClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Outlined.BugReport,
                contentDescription = "Debug",
                tint = iconTint
            )
        }
    }

    Box(
        modifier = modifier
            .size(60.dp)
            .padding(end = 15.dp)
            .clickable(onClick = onLangClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_lang_button),
            contentDescription = "Language",
            tint = iconTint
        )
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clickable(onClick = { toggleDarkTheme() }),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = if (LocalDarkTheme.current) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
            contentDescription = "Dark/Light mode",
            tint = iconTint
        )
    }
}