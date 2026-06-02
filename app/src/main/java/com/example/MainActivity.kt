package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AddDownloadScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.model.DownloadManager

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    DownloadManager.init(this)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          val navController = rememberNavController()
          NavHost(
            navController = navController,
            startDestination = "splash",
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
          ) {
            composable("splash") { SplashScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("add_download?url={url}") { backStackEntry -> 
              val url = backStackEntry.arguments?.getString("url")
              AddDownloadScreen(navController, url) 
            }
            composable("downloads") { DownloadsScreen(navController) }
            composable("history") { HistoryScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
          }
        }
      }
    }
  }
}
