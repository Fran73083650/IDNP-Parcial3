package com.example.parcial3idnp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parcial3idnp.data.local.ActivityDatabase
import com.example.parcial3idnp.data.repository.ActivityRepository
import com.example.parcial3idnp.notification.NotificationHelper
import com.example.parcial3idnp.ui.screens.ActivityListScreen
import com.example.parcial3idnp.ui.screens.AddActivityScreen
import com.example.parcial3idnp.ui.screens.SplashScreen
import com.example.parcial3idnp.ui.theme.Parcial3IDNPTheme
import com.example.parcial3idnp.ui.viewmodel.ActivityViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.createNotificationChannel(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val database = ActivityDatabase.getDatabase(this)
        val repository = ActivityRepository(database.activityDao())

        setContent {
            Parcial3IDNPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
                        SplashScreen(onTimeout = { showSplash = false })
                    } else {
                        AppNavigation(repository)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(repository: ActivityRepository) {
    val navController = rememberNavController()

    val viewModel: ActivityViewModel = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ActivityViewModel(repository) as T
            }
        }.create(ActivityViewModel::class.java)
    }

    NavHost(
        navController = navController,
        startDestination = "activity_list"
    ) {
        composable("activity_list") {
            ActivityListScreen(
                viewModel = viewModel,
                onAddClick = {
                    navController.navigate("add_activity/-1")
                },
                onEditClick = { activityId ->
                    navController.navigate("add_activity/$activityId")
                }
            )
        }

        composable(
            route = "add_activity/{activityId}",
            arguments = listOf(navArgument("activityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getInt("activityId") ?: -1

            AddActivityScreen(
                viewModel = viewModel,
                activityId = if (activityId == -1) null else activityId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}