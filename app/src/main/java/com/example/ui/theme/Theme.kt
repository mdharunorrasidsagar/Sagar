package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CustomLightColorScheme = lightColorScheme(
    primary = MintGreen,
    secondary = SkyBlue,
    tertiary = SoftCoral,
    background = BackgroundWhite,
    surface = OffWhiteSurface,
    onPrimary = BackgroundWhite,
    onSecondary = BackgroundWhite,
    onTertiary = BackgroundWhite,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal,
    primaryContainer = MintGreenLight,
    secondaryContainer = SkyBlueLight,
    tertiaryContainer = SoftCoralLight,
    outline = DividerGray
)

@Composable
fun HealthTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CustomLightColorScheme,
        typography = Typography,
        content = content
    )
}
