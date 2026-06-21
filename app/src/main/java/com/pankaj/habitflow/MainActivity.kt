package com.pankaj.habitflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pankaj.habitflow.presentation.navigation.MainScreen
import com.pankaj.habitflow.presentation.screen.settings.SettingsViewModel
import com.pankaj.habitflow.presentation.theme.HabitFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()
            HabitFlowTheme(themeMode = themeMode) {
                MainScreen()
            }
        }
    }
}
