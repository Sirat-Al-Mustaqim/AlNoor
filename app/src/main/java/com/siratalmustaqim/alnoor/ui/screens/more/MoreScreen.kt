package com.siratalmustaqim.alnoor.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.siratalmustaqim.alnoor.R
import com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme

data class MoreMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Composable
fun MoreScreen(
    onQuranClick: () -> Unit = {},
    onDuasClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val menuItems = listOf(
        MoreMenuItem(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            title = stringResource(R.string.more_quran_title),
            subtitle = stringResource(R.string.more_quran_subtitle),
            onClick = onQuranClick
        ),
        MoreMenuItem(
            icon = Icons.Filled.AutoStories,
            title = stringResource(R.string.more_duas_title),
            subtitle = stringResource(R.string.more_duas_subtitle),
            onClick = onDuasClick
        ),
        MoreMenuItem(
            icon = Icons.Filled.Settings,
            title = stringResource(R.string.more_settings_title),
            subtitle = stringResource(R.string.more_settings_subtitle),
            onClick = onSettingsClick
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.more_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        menuItems.forEach { item ->
            MoreMenuCard(item = item)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun MoreMenuCard(item: MoreMenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.more_go_to, item.title),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoreScreenPreview() {
    AlNoorTheme {
        MoreScreen()
    }
}
