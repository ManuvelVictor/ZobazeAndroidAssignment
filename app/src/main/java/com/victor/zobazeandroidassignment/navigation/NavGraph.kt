package com.victor.zobazeandroidassignment.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.victor.zobazeandroidassignment.screens.ExpenseEntryScreen
import com.victor.zobazeandroidassignment.screens.ExpenseListScreen
import com.victor.zobazeandroidassignment.screens.ExpenseReportScreen
import com.victor.zobazeandroidassignment.viewmodel.ExpenseViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = NavRoutes.ENTRY
    ) {
        composable(NavRoutes.ENTRY) {
            val viewModel: ExpenseViewModel = hiltViewModel()
            ExpenseEntryScreen(navController, viewModel)
        }
        composable(NavRoutes.LIST) {
            val viewModel: ExpenseViewModel = hiltViewModel()
            ExpenseListScreen(navController, viewModel)
        }
        composable(NavRoutes.REPORT) {
            val viewModel: ExpenseViewModel = hiltViewModel()
            ExpenseReportScreen(navController, viewModel)
        }
    }
}
