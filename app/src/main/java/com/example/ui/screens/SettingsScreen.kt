package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.components.AppBottomNavigation

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var downloadPath by remember {
        mutableStateOf(prefs.getString("download_path", "/Internal Storage/Donwloads/BestVID") ?: "/Internal Storage/Donwloads/BestVID")
    }
    var showPathDialog by remember { mutableStateOf(false) }

    val docLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            val path = uri.path ?: ""
            val cleanPath = if (path.contains(":")) {
                val parts = path.split(":")
                if (parts.size > 1) "/Internal Storage/${parts[1]}" else "/Internal Storage/$path"
            } else {
                "/Internal Storage/$path"
            }
            downloadPath = cleanPath
            prefs.edit().putString("download_path", cleanPath).apply()
        }
    }

    if (showPathDialog) {
        AlertDialog(
            onDismissRequest = { showPathDialog = false },
            icon = { Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp)) },
            title = { Text("Select Download Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select where media files should be saved:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        onClick = {
                            val path = "/Internal Storage/Downloads/BestVID"
                            downloadPath = path
                            prefs.edit().putString("download_path", path).apply()
                            showPathDialog = false
                        },
                        shape = MaterialTheme.shapes.small,
                        color = if (downloadPath == "/Internal Storage/Downloads/BestVID") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("System Downloads Folder", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("/Internal Storage/Downloads/BestVID", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Surface(
                        onClick = {
                            val path = "/Internal Storage/Movies/BestVID"
                            downloadPath = path
                            prefs.edit().putString("download_path", path).apply()
                            showPathDialog = false
                        },
                        shape = MaterialTheme.shapes.small,
                        color = if (downloadPath == "/Internal Storage/Movies/BestVID") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Movies Directory", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("/Internal Storage/Movies/BestVID", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Surface(
                        onClick = {
                            val path = "/Internal Storage/Music/BestVID"
                            downloadPath = path
                            prefs.edit().putString("download_path", path).apply()
                            showPathDialog = false
                        },
                        shape = MaterialTheme.shapes.small,
                        color = if (downloadPath == "/Internal Storage/Music/BestVID") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Music Directory", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("/Internal Storage/Music/BestVID", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Button(
                        onClick = {
                            showPathDialog = false
                            try {
                                docLauncher.launch(null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Custom Folder (System Picker)", style = MaterialTheme.typography.labelMedium)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPathDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = { SettingsTopBar() },
        bottomBar = { AppBottomNavigation(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { UserProfileSection() }
            item { SettingsCategory(title = "GENERAL") {
                SettingItemNavigation(
                    title = "Download path",
                    iconName = "folder",
                    subtitle = downloadPath,
                    onClick = { showPathDialog = true }
                )
                Divider()
                SettingItemSwitch("Auto-paste link", "content_paste", "Automatically detect URLs in clipboard", true)
                Divider()
                SettingItemSwitch("Dark theme", "dark_mode", null, true)
            } }
            item { SettingsCategory(title = "NETWORK") {
                SettingItemSwitch("Wi-Fi only", "wifi", "Pause downloads on cellular data", true)
                Divider()
                SettingItemNavigation("Concurrent downloads", "speed", "1 sequential task (serial queue)")
            } }
            item { StorageSection() }
            item { SettingsCategory(title = "ABOUT") {
                SettingItemNavigation("Version", "info", "v2.4.0 (Stable)", showArrow = false)
                Divider()
                SettingItemNavigation("Privacy Policy", "description", null, iconRight = Icons.Default.OpenInNew)
                Divider()
                SettingItemNavigation("Rate Best VID", "star", null, showArrow = false)
            } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DownloadForOffline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Best VID", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun UserProfileSection() {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(12.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Guest User", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                    Text("Sign in to sync downloads", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Button(
                onClick = { },
                shape = androidx.compose.foundation.shape.CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Sign In", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingItemNavigation(
    title: String, 
    iconName: String, 
    subtitle: String? = null, 
    showArrow: Boolean = true, 
    iconRight: ImageVector? = Icons.Default.ChevronRight,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when (iconName) {
                "folder" -> Icons.Default.Folder
                "speed" -> Icons.Default.Speed
                "info" -> Icons.Default.Info
                "description" -> Icons.Default.Description
                "star" -> Icons.Default.Star
                else -> Icons.Default.Settings
            }
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (showArrow && iconRight != null) {
            Icon(iconRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun SettingItemSwitch(title: String, iconName: String, subtitle: String? = null, checked: Boolean) {
    var isChecked by remember { mutableStateOf(checked) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            val icon = when (iconName) {
                "content_paste" -> Icons.Default.ContentPaste
                "dark_mode" -> Icons.Default.DarkMode
                "wifi" -> Icons.Default.Wifi
                else -> Icons.Default.Settings
            }
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
            )
        )
    }
}

@Composable
fun Divider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))
}

@Composable
fun StorageSection() {
    SettingsCategory(title = "STORAGE") {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Available Storage", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("42.5 GB / 128 GB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.33f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear App Cache", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
