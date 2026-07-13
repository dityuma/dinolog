package com.dites.dinolog

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.dites.dinolog.ui.navigation.NavGraph
import com.dites.dinolog.ui.theme.AppTheme
import com.dites.dinolog.ui.theme.DinoLogTheme
import com.dites.dinolog.ui.theme.ThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        val cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Permission granted or denied — app continues either way
            // No action needed here, just reduces crash risk later
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as DinoLogApplication).repository
        val themePreference = ThemePreference(this)

        var isReady by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            val themeName by themePreference.selectedTheme
                .collectAsState(initial = ThemePreference.DEFAULT_THEME)

            LaunchedEffect(themeName) {
                isReady = true
            }

            val appTheme = try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.ALDABRA_GIANT
            }

            DinoLogTheme(appTheme = appTheme) {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
}
