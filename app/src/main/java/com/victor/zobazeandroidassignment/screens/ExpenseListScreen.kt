package com.victor.zobazeandroidassignment.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.victor.zobazeandroidassignment.navigation.NavRoutes
import com.victor.zobazeandroidassignment.utils.BottomNavigationBar
import com.victor.zobazeandroidassignment.utils.DatePickerSection
import com.victor.zobazeandroidassignment.utils.EmptyLottie
import com.victor.zobazeandroidassignment.utils.ExpenseCard
import com.victor.zobazeandroidassignment.utils.Grouping
import com.victor.zobazeandroidassignment.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expensesFlow.collectAsState()

// UI state
    var showDatePicker by remember { mutableStateOf(false) }
    var grouping by remember { mutableStateOf(Grouping.Time) } // Time or Category

    val totalAmount = remember(expenses) { expenses.sumOf { it.amount } }
    val dateLabel = remember(viewModel.selectedDate.collectAsState().value) {
        val fmt = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        fmt.format(Date(viewModel.selectedDate.value))
    }

// Bottom sheet states
    val datePickerSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense List") },
                actions = {
                    // Date chooser
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date")
                    }
                    // Grouping toggle
                    IconButton(onClick = {
                        grouping =
                            if (grouping == Grouping.Time) Grouping.Category else Grouping.Time
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Toggle Group")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = NavRoutes.LIST,
                onNavItemSelected = { route ->
                    if (route != NavRoutes.LIST) {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Header: date label, totals
            Text(
                text = "$dateLabel",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Count: ${expenses.size}   |   Total: ₹$totalAmount",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))

            if (expenses.isEmpty()) {
                // Centered Lottie empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyLottie()
                }
            } else {
                when (grouping) {
                    Grouping.Time -> {
                        // Flat list by time (already sorted in DAO; if not, sort here by date desc)
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(expenses) { expense ->
                                ExpenseCard(
                                    title = expense.title,
                                    amount = expense.amount,
                                    subtitleTop = expense.category,
                                    subtitleBottom = expense.notes
                                )
                            }
                        }
                    }

                    Grouping.Category -> {
                        // Group by category
                        val grouped = remember(expenses) {
                            expenses.groupBy { it.category }
                                .toSortedMap(String.CASE_INSENSITIVE_ORDER)
                        }
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            grouped.forEach { (category, list) ->
                                item(key = "header_$category") {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Spacer(Modifier.height(4.dp))
                                }
                                items(list, key = { it.id }) { expense ->
                                    ExpenseCard(
                                        title = expense.title,
                                        amount = expense.amount,
                                        subtitleTop = SimpleDateFormat(
                                            "hh:mm a",
                                            Locale.getDefault()
                                        ).format(Date(expense.date)),
                                        subtitleBottom = expense.notes
                                    )
                                }
                                item(key = "divider_$category") { Spacer(Modifier.height(4.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }

// Date picker bottom sheet
    if (showDatePicker) {
        ModalBottomSheet(
            onDismissRequest = { showDatePicker = false },
            sheetState = datePickerSheet
        ) {
            DatePickerSection(
                initial = viewModel.selectedDate.collectAsState().value,
                onConfirm = { pickedMillis ->
                    viewModel.setDate(pickedMillis)
                    showDatePicker = false
                },
                onCancel = { showDatePicker = false }
            )
        }
    }
}



