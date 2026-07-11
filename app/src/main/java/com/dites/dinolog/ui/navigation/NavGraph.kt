package com.dites.dinolog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.screen.*

sealed class Screen(val route: String) {
    object ReptileList : Screen("reptile_list")
    object AddReptile : Screen("add_reptile")
    object ReptileDetail : Screen("reptile_detail/{reptileId}") {
        fun createRoute(reptileId: Long) = "reptile_detail/$reptileId"
    }
    object EditReptile : Screen("edit_reptile/{reptileId}") {
        fun createRoute(reptileId: Long) = "edit_reptile/$reptileId"
    }
    object AddGrowthLog : Screen("add_growth_log/{reptileId}") {
        fun createRoute(reptileId: Long) = "add_growth_log/$reptileId"
    }
    object EditGrowthLog : Screen("edit_growth_log/{reptileId}/{logId}") {
        fun createRoute(reptileId: Long, logId: Long) = "edit_growth_log/$reptileId/$logId"
    }
    object AddFeedingLog : Screen("add_feeding_log/{reptileId}") {
        fun createRoute(reptileId: Long) = "add_feeding_log/$reptileId"
    }
    object EditFeedingLog : Screen("edit_feeding_log/{reptileId}/{logId}") {
        fun createRoute(reptileId: Long, logId: Long) = "edit_feeding_log/$reptileId/$logId"
    }
    object AddScuteLog : Screen("add_scute_log/{reptileId}") {
        fun createRoute(reptileId: Long) = "add_scute_log/$reptileId"
    }
    object EditScuteLog : Screen("edit_scute_log/{reptileId}/{logId}") {
        fun createRoute(reptileId: Long, logId: Long) = "edit_scute_log/$reptileId/$logId"
    }
    object AddBrumasiLog : Screen("add_brumasi_log/{reptileId}") {
        fun createRoute(reptileId: Long) = "add_brumasi_log/$reptileId"
    }
    object AddRiwayat : Screen("add_riwayat/{reptileId}") {
        fun createRoute(reptileId: Long) = "add_riwayat/$reptileId"
    }
    object EditRiwayat : Screen("edit_riwayat/{reptileId}/{riwayatId}") {
        fun createRoute(reptileId: Long, riwayatId: Long) = "edit_riwayat/$reptileId/$riwayatId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: DinoLogRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ReptileList.route,
        modifier = modifier
    ) {
        composable(Screen.ReptileList.route) {
            ReptileListScreen(
                repository = repository,
                onNavigateToAddReptile = {
                    navController.navigate(Screen.AddReptile.route)
                },
                onNavigateToDetail = { reptileId ->
                    navController.navigate(Screen.ReptileDetail.createRoute(reptileId))
                }
            )
        }
        composable(Screen.AddReptile.route) {
            AddReptileScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.ReptileDetail.route,
            arguments = listOf(navArgument("reptileId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            ReptileDetailScreen(
                reptileId = reptileId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddGrowth = { id -> navController.navigate(Screen.AddGrowthLog.createRoute(id)) },
                onNavigateToEditGrowth = { rid, lid -> navController.navigate(Screen.EditGrowthLog.createRoute(rid, lid)) },
                onNavigateToAddFeeding = { id -> navController.navigate(Screen.AddFeedingLog.createRoute(id)) },
                onNavigateToEditFeeding = { rid, lid -> navController.navigate(Screen.EditFeedingLog.createRoute(rid, lid)) },
                onNavigateToAddScute = { id -> navController.navigate(Screen.AddScuteLog.createRoute(id)) },
                onNavigateToEditScute = { rid, lid -> navController.navigate(Screen.EditScuteLog.createRoute(rid, lid)) },
                onNavigateToAddRiwayat = { id -> navController.navigate(Screen.AddRiwayat.createRoute(id)) },
                onNavigateToEditRiwayat = { rid, lid -> navController.navigate(Screen.EditRiwayat.createRoute(rid, lid)) },
                onNavigateToAddBrumasi = { id -> navController.navigate(Screen.AddBrumasiLog.createRoute(id)) },
                onNavigateToEditReptile = { id -> navController.navigate(Screen.EditReptile.createRoute(id)) }
            )
        }
        composable(
            route = Screen.EditReptile.route,
            arguments = listOf(navArgument("reptileId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            EditReptileScreen(
                reptileId = reptileId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.AddGrowthLog.route,
            arguments = listOf(navArgument("reptileId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            AddGrowthLogScreen(
                reptileId = reptileId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.AddFeedingLog.route,
            arguments = listOf(navArgument("reptileId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            AddFeedingLogScreen(
                reptileId = reptileId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditFeedingLog.route,
            arguments = listOf(
                navArgument("reptileId") { type = NavType.LongType },
                navArgument("logId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            val logId = backStackEntry.arguments?.getLong("logId") ?: return@composable
            EditFeedingLogScreen(
                reptileId = reptileId,
                logId = logId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.AddScuteLog.route,
            arguments = listOf(navArgument("reptileId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            AddScuteLogScreen(
                reptileId = reptileId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditScuteLog.route,
            arguments = listOf(
                navArgument("reptileId") { type = NavType.LongType },
                navArgument("logId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            val logId = backStackEntry.arguments?.getLong("logId") ?: return@composable
            EditScuteLogScreen(
                reptileId = reptileId,
                logId = logId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.AddBrumasiLog.route,
            arguments = listOf(navArgument("reptileId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            AddBrumasiLogScreen(
                reptileId = reptileId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.AddRiwayat.route,
            arguments = listOf(navArgument("reptileId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            AddRiwayatScreen(
                reptileId = reptileId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditRiwayat.route,
            arguments = listOf(
                navArgument("reptileId") { type = NavType.LongType },
                navArgument("riwayatId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            val riwayatId = backStackEntry.arguments?.getLong("riwayatId") ?: return@composable
            EditRiwayatScreen(
                reptileId = reptileId,
                riwayatId = riwayatId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditGrowthLog.route,
            arguments = listOf(
                navArgument("reptileId") { type = NavType.LongType },
                navArgument("logId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val reptileId = backStackEntry.arguments?.getLong("reptileId") ?: return@composable
            val logId = backStackEntry.arguments?.getLong("logId") ?: return@composable
            EditGrowthLogScreen(
                reptileId = reptileId,
                logId = logId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
