package com.penguinlearner.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = PenguinWhite,
    primaryContainer = NavyBlueLight,
    onPrimaryContainer = PenguinWhite,

    secondary = PenguinYellow,
    onSecondary = NavyBlueDark,
    secondaryContainer = PenguinYellow.copy(alpha = 0.3f),
    onSecondaryContainer = NavyBlueDark,

    tertiary = PenguinOrange,
    onTertiary = PenguinWhite,
    tertiaryContainer = PenguinOrange.copy(alpha = 0.3f),
    onTertiaryContainer = NavyBlueDark,

    background = SnowWhite,
    onBackground = NavyBlueDark,

    surface = PenguinWhite,
    onSurface = NavyBlueDark,
    surfaceVariant = IceBlue,
    onSurfaceVariant = NavyBlue,

    error = WrongRed,
    onError = PenguinWhite,
    errorContainer = WrongRedLight,
    onErrorContainer = WrongRed,

    outline = PenguinGray,
    outlineVariant = PenguinLightGray
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyBlueLight,
    onPrimary = PenguinWhite,
    primaryContainer = NavyBlue,
    onPrimaryContainer = PenguinWhite,

    secondary = PenguinYellow,
    onSecondary = NavyBlueDark,
    secondaryContainer = PenguinYellow.copy(alpha = 0.3f),
    onSecondaryContainer = PenguinYellow,

    tertiary = PenguinOrange,
    onTertiary = PenguinWhite,
    tertiaryContainer = PenguinOrange.copy(alpha = 0.3f),
    onTertiaryContainer = PenguinOrange,

    background = NavyBlueDark,
    onBackground = PenguinWhite,

    surface = NavyBlueDark,
    onSurface = PenguinWhite,
    surfaceVariant = NavyBlue,
    onSurfaceVariant = PenguinLightGray,

    error = WrongRed,
    onError = PenguinWhite,
    errorContainer = WrongRedLight.copy(alpha = 0.2f),
    onErrorContainer = WrongRed,

    outline = PenguinGray,
    outlineVariant = PenguinDarkGray
)

@Composable
fun PenguinLearnerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
