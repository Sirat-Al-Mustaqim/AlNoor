package com.siratalmustaqim.alnoor.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * A wrapper composable that sets the layout direction to RTL (Right-to-Left)
 * for all its children. Useful for Arabic text and content.
 *
 * Usage:
 * ```
 * RtlLayout {
 *     Text("Arabic text here")
 *     // All children will use RTL layout direction
 * }
 * ```
 */
@Composable
fun RtlLayout(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        content()
    }
}

/**
 * A wrapper composable that sets the layout direction to LTR (Left-to-Right).
 * Useful when you need to override RTL back to LTR for specific content.
 */
@Composable
fun LtrLayout(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        content()
    }
}
