package com.dites.dinolog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dites.dinolog.ui.navigation.NavGraph
import com.dites.dinolog.ui.theme.AppTheme
import com.dites.dinolog.ui.theme.DinoLogTheme
import com.dites.dinolog.ui.theme.ThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as DinoLogApplication).repository
        val themePreference = ThemePreference(this)

        setContent {
            val themeName by themePreference.selectedTheme
                .collectAsState(initial = ThemePreference.DEFAULT_THEME)

            val appTheme = try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.CITRUS_SPARK
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
    }
}
