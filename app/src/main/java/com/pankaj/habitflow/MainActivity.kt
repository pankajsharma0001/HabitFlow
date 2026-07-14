package com.pankaj.habitflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.pankaj.habitflow.presentation.navigation.MainScreen
import com.pankaj.habitflow.presentation.screen.settings.BiometricLockScreen
import com.pankaj.habitflow.presentation.screen.settings.SettingsViewModel
import com.pankaj.habitflow.presentation.theme.HabitFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled automatically by OS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        requestNotificationPermission()

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val biometricEnabled by settingsViewModel.biometricEnabled.collectAsState()
            var isAuthenticated by remember { mutableStateOf(false) }

            HabitFlowTheme(themeMode = themeMode) {
                if (biometricEnabled && !isAuthenticated) {
                    BiometricLockScreen(
                        onAuthenticated = { isAuthenticated = true }
                    )
                } else {
                    MainScreen()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
