package com.pankaj.habitflow.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pankaj.habitflow.presentation.screen.habits.AddEditHabitScreen
import com.pankaj.habitflow.presentation.screen.habits.AddEditHabitViewModel
import com.pankaj.habitflow.presentation.screen.habits.HabitsScreen
import com.pankaj.habitflow.presentation.screen.habits.HabitsViewModel
import com.pankaj.habitflow.presentation.screen.settings.SettingsScreen
import com.pankaj.habitflow.presentation.screen.settings.SettingsViewModel
import com.pankaj.habitflow.presentation.screen.stats.StatsScreen
import com.pankaj.habitflow.presentation.screen.stats.StatsViewModel
import com.pankaj.habitflow.presentation.screen.today.TodayScreen
import com.pankaj.habitflow.presentation.screen.today.TodayViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Today : Screen("today", "Today", Icons.Default.Today)
    object Stats : Screen("stats", "Stats", Icons.Default.Analytics)
    object Habits : Screen("habits", "Habits", Icons.Default.List)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Today.route,
        modifier = modifier
    ) {
        composable(Screen.Today.route) {
            val viewModel: TodayViewModel = hiltViewModel()
            TodayScreen(
                viewModel = viewModel,
                onAddHabitClick = {
                    navController.navigate("add_edit_habit/new")
                },
                onHabitClick = { habitId ->
                    navController.navigate("add_edit_habit/$habitId")
                }
            )
        }

        composable(Screen.Stats.route) {
            val viewModel: StatsViewModel = hiltViewModel()
            StatsScreen(viewModel = viewModel)
        }

        composable(Screen.Habits.route) {
            val viewModel: HabitsViewModel = hiltViewModel()
            HabitsScreen(
                viewModel = viewModel,
                onAddHabitClick = {
                    navController.navigate("add_edit_habit/new")
                },
                onHabitClick = { habitId ->
                    navController.navigate("add_edit_habit/$habitId")
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(viewModel = viewModel)
        }

        composable(
            route = "add_edit_habit/{habitId}",
            arguments = listOf(
                navArgument("habitId") {
                    type = NavType.StringType
                    defaultValue = "new"
                }
            )
        ) {
            val viewModel: AddEditHabitViewModel = hiltViewModel()
            AddEditHabitScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(
        Screen.Today,
        Screen.Stats,
        Screen.Habits,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in items.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
