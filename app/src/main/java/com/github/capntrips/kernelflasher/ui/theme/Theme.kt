package com.github.capntrips.kernelflasher.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import com.github.capntrips.kernelflasher.ui.theme.primaryLight
import com.github.capntrips.kernelflasher.ui.theme.onPrimaryLight
import com.github.capntrips.kernelflasher.ui.theme.primaryContainerLight
import com.github.capntrips.kernelflasher.ui.theme.onPrimaryContainerLight
import com.github.capntrips.kernelflasher.ui.theme.secondaryLight
import com.github.capntrips.kernelflasher.ui.theme.onSecondaryLight
import com.github.capntrips.kernelflasher.ui.theme.secondaryContainerLight
import com.github.capntrips.kernelflasher.ui.theme.onSecondaryContainerLight
import com.github.capntrips.kernelflasher.ui.theme.tertiaryLight
import com.github.capntrips.kernelflasher.ui.theme.onTertiaryLight
import com.github.capntrips.kernelflasher.ui.theme.tertiaryContainerLight
import com.github.capntrips.kernelflasher.ui.theme.onTertiaryContainerLight
import com.github.capntrips.kernelflasher.ui.theme.errorLight
import com.github.capntrips.kernelflasher.ui.theme.onErrorLight
import com.github.capntrips.kernelflasher.ui.theme.errorContainerLight
import com.github.capntrips.kernelflasher.ui.theme.onErrorContainerLight
import com.github.capntrips.kernelflasher.ui.theme.backgroundLight
import com.github.capntrips.kernelflasher.ui.theme.onBackgroundLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceLight
import com.github.capntrips.kernelflasher.ui.theme.onSurfaceLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceVariantLight
import com.github.capntrips.kernelflasher.ui.theme.onSurfaceVariantLight
import com.github.capntrips.kernelflasher.ui.theme.outlineLight
import com.github.capntrips.kernelflasher.ui.theme.outlineVariantLight
import com.github.capntrips.kernelflasher.ui.theme.scrimLight
import com.github.capntrips.kernelflasher.ui.theme.inverseSurfaceLight
import com.github.capntrips.kernelflasher.ui.theme.inverseOnSurfaceLight
import com.github.capntrips.kernelflasher.ui.theme.inversePrimaryLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceDimLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceBrightLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerLowestLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerLowLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerHighLight
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerHighestLight
import com.github.capntrips.kernelflasher.ui.theme.primaryDark
import com.github.capntrips.kernelflasher.ui.theme.onPrimaryDark
import com.github.capntrips.kernelflasher.ui.theme.primaryContainerDark
import com.github.capntrips.kernelflasher.ui.theme.onPrimaryContainerDark
import com.github.capntrips.kernelflasher.ui.theme.secondaryDark
import com.github.capntrips.kernelflasher.ui.theme.onSecondaryDark
import com.github.capntrips.kernelflasher.ui.theme.secondaryContainerDark
import com.github.capntrips.kernelflasher.ui.theme.onSecondaryContainerDark
import com.github.capntrips.kernelflasher.ui.theme.tertiaryDark
import com.github.capntrips.kernelflasher.ui.theme.onTertiaryDark
import com.github.capntrips.kernelflasher.ui.theme.tertiaryContainerDark
import com.github.capntrips.kernelflasher.ui.theme.onTertiaryContainerDark
import com.github.capntrips.kernelflasher.ui.theme.errorDark
import com.github.capntrips.kernelflasher.ui.theme.onErrorDark
import com.github.capntrips.kernelflasher.ui.theme.errorContainerDark
import com.github.capntrips.kernelflasher.ui.theme.onErrorContainerDark
import com.github.capntrips.kernelflasher.ui.theme.backgroundDark
import com.github.capntrips.kernelflasher.ui.theme.onBackgroundDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceDark
import com.github.capntrips.kernelflasher.ui.theme.onSurfaceDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceVariantDark
import com.github.capntrips.kernelflasher.ui.theme.onSurfaceVariantDark
import com.github.capntrips.kernelflasher.ui.theme.outlineDark
import com.github.capntrips.kernelflasher.ui.theme.outlineVariantDark
import com.github.capntrips.kernelflasher.ui.theme.scrimDark
import com.github.capntrips.kernelflasher.ui.theme.inverseSurfaceDark
import com.github.capntrips.kernelflasher.ui.theme.inverseOnSurfaceDark
import com.github.capntrips.kernelflasher.ui.theme.inversePrimaryDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceDimDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceBrightDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerLowestDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerLowDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerHighDark
import com.github.capntrips.kernelflasher.ui.theme.surfaceContainerHighestDark

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun KernelFlasherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}