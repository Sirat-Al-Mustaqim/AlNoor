package com.siratalmustaqim.alnoor.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siratalmustaqim.alnoor.BuildConfig
import com.siratalmustaqim.alnoor.R
import com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme
import com.siratalmustaqim.alnoor.ui.theme.Gold
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    // Animation states
    val logoScale = remember { Animatable(0.8f) }
    val logoAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val footerAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo animation
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )

        // Content fade in
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, delayMillis = 200)
        )

        // Footer fade in
        footerAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, delayMillis = 100)
        )

        // Wait before navigating
        delay(1500)
        onSplashComplete()
    }

    SplashScreenContent(
        logoScale = logoScale.value,
        logoAlpha = logoAlpha.value,
        contentAlpha = contentAlpha.value,
        footerAlpha = footerAlpha.value
    )
}

@Composable
private fun SplashScreenContent(
    logoScale: Float = 1f,
    logoAlpha: Float = 1f,
    contentAlpha: Float = 1f,
    footerAlpha: Float = 1f
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle dot pattern background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.03f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )

        // Main content - centered
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Composition
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
            ) {
                // Logo image
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Typography Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(contentAlpha)
            ) {
                // App Name
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tagline with decorative lines
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Left decorative line
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(1.dp)
                            .background(Gold.copy(alpha = 0.5f))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Tagline
                    Text(
                        text = stringResource(R.string.splash_tagline).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right decorative line
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(1.dp)
                            .background(Gold.copy(alpha = 0.5f))
                    )
                }
            }
        }

        // Footer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(footerAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Security Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.splash_offline_protection),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version
            Text(
                text = "v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    AlNoorTheme {
        SplashScreenContent()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SplashScreenDarkPreview() {
    AlNoorTheme(darkTheme = true) {
        SplashScreenContent()
    }
}
