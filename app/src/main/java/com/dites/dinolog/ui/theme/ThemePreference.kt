package com.dites.dinolog.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class ThemePreference(private val context: Context) {
    
    companion object {
        val THEME_KEY = stringPreferencesKey("selected_theme")
        const val DEFAULT_THEME = "SULCATA_DESERT"
    }
    
    val selectedTheme: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[THEME_KEY] ?: DEFAULT_THEME }
    
    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}
