package com.siratalmustaqim.alnoor.ui.screens.verification

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.siratalmustaqim.alnoor.R
import com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme
import com.siratalmustaqim.alnoor.ui.theme.Gold

@Composable
fun AyahVerificationScreen(
    onVerificationSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AyahVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val successMessage = stringResource(R.string.verification_success)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AyahVerificationEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is AyahVerificationEvent.VerificationSuccess -> {
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                    onVerificationSuccess()
                }

                is AyahVerificationEvent.VerificationFailed -> {
                    // Toast already shown in ShowToast event
                }
            }
        }
    }

    AyahVerificationScreenContent(
        uiState = uiState,
        onInputChange = { newValue ->
            if (newValue.length - uiState.currentInput.length > 1) {
                Toast.makeText(
                    context,
                    context.getString(R.string.verification_paste_disabled_toast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                uiState.onInputChange(newValue)
            }
        },
        onCancel = onCancel
    )
}

@Composable
private fun AyahVerificationScreenContent(
    uiState: AyahVerificationUiState,
    onInputChange: (String) -> Unit,
    onCancel: () -> Unit
) {
    val progress = uiState.enteredAyahs.size / 13f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top AppBar
            TopAppBar(onBack = onCancel)

            // Scrollable content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 140.dp)
            ) {
                // Progress Section
                item {
                    ProgressSection(
                        enteredCount = uiState.enteredAyahs.size,
                        progress = progress
                    )
                }

                // Gradient Divider
                item {
                    GradientDivider()
                }

                // Arabic Input Section
                item {
                    ArabicInputSection(
                        currentInput = uiState.currentInput,
                        onInputChange = onInputChange,
                        isEnabled = uiState.enteredAyahs.size < 13,
                        onDone = uiState.onAddAyah
                    )
                }

                // Add Button
                item {
                    AddAyahButton(
                        onClick = uiState.onAddAyah,
                        isEnabled = uiState.currentInput.isNotBlank() && uiState.enteredAyahs.size < 13
                    )
                }

                // Verification Sequence Section Header
                if (uiState.enteredAyahs.isNotEmpty()) {
                    item {
                        SequenceHeader(onClearAll = uiState.onClearAll)
                    }

                    // Ayah Cards
                    itemsIndexed(uiState.enteredAyahs) { index, ayah ->
                        AyahCard(
                            index = index + 1,
                            text = ayah,
                            isLast = index == uiState.enteredAyahs.lastIndex,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }

        // Sticky Footer
        StickyFooter(
            canValidate = uiState.canValidate,
            isValidating = uiState.isValidating,
            onValidate = uiState.onValidate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TopAppBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = stringResource(R.string.cd_back),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = stringResource(R.string.verification_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .padding(end = 40.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProgressSection(
    enteredCount: Int,
    progress: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = stringResource(R.string.verification_session_progress).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.verification_ayahs_added),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$enteredCount",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "/13",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.verification_progress_hint),
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GradientDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun ArabicInputSection(
    currentInput: String,
    onInputChange: (String) -> Unit,
    isEnabled: Boolean,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Header row with labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.verification_arabic_input).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            // Pasting Disabled Badge
            Text(
                text = stringResource(R.string.verification_pasting_disabled).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                fontWeight = FontWeight.Bold,
                color = Gold,
                letterSpacing = 0.5.sp,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Gold.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Text Input Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.02f))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(20.dp)
        ) {
            BasicTextField(
                value = currentInput,
                onValueChange = onInputChange,
                enabled = isEnabled,
                modifier = Modifier.fillMaxSize(),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 32.sp
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (currentInput.isEmpty()) {
                            Text(
                                text = stringResource(R.string.verification_input_placeholder),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun AddAyahButton(
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Button(
            onClick = onClick,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.verification_add_ayah).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun SequenceHeader(onClearAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.verification_sequence).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onClearAll() }
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.verification_clear_all),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AyahCard(
    index: Int,
    text: String,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isLast) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isLast) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    val badgeBgColor = if (isLast) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        Gold.copy(alpha = 0.1f)
    }

    val badgeTextColor = if (isLast) {
        MaterialTheme.colorScheme.primary
    } else {
        Gold
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isLast) 2.dp else 1.dp,
            color = borderColor
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Index Badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", index),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }

            // Arabic Text
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    lineHeight = 28.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StickyFooter(
    canValidate: Boolean,
    isValidating: Boolean,
    onValidate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = onValidate,
                enabled = canValidate && !isValidating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canValidate) Gold else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (canValidate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                if (isValidating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (canValidate) Icons.Filled.ShieldMoon else Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (canValidate) {
                            stringResource(R.string.verification_activate_protection).uppercase()
                        } else {
                            stringResource(R.string.verification_validate_locked)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (canValidate) 2.sp else 0.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.verification_footer).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                letterSpacing = 3.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AyahVerificationScreenPreview() {
    AlNoorTheme {
        AyahVerificationScreenContent(
            uiState = AyahVerificationUiState(),
            onInputChange = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AyahVerificationScreenWithAyahsPreview() {
    AlNoorTheme {
        AyahVerificationScreenContent(
            uiState = AyahVerificationUiState(
                enteredAyahs = listOf(
                    "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                    "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    "الرَّحْمَـٰنِ الرَّحِيمِ",
                    "مَالِكِ يَوْمِ الدِّينِ"
                )
            ),
            onInputChange = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AyahVerificationScreenDarkPreview() {
    AlNoorTheme(darkTheme = true) {
        AyahVerificationScreenContent(
            uiState = AyahVerificationUiState(
                enteredAyahs = listOf(
                    "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                    "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ"
                )
            ),
            onInputChange = {},
            onCancel = {}
        )
    }
}
