package com.victor.zobazeandroidassignment.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.victor.zobazeandroidassignment.navigation.NavRoutes
import com.victor.zobazeandroidassignment.utils.BottomNavigationBar
import com.victor.zobazeandroidassignment.utils.CategoryBarChart
import com.victor.zobazeandroidassignment.utils.CategoryTotalsList
import com.victor.zobazeandroidassignment.utils.Last7DaysChart
import com.victor.zobazeandroidassignment.utils.Last7DaysList
import com.victor.zobazeandroidassignment.utils.shareFile
import com.victor.zobazeandroidassignment.utils.show
import com.victor.zobazeandroidassignment.utils.writeCsv
import com.victor.zobazeandroidassignment.utils.writeMockPdf
import com.victor.zobazeandroidassignment.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val categoryTotals by viewModel.categoryTotalsFlow.collectAsState()
    val last7Days by viewModel.last7DaysFlow.collectAsState()
    val last7Sum = remember(last7Days) { last7Days.sumOf { it.second } }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

// File creation launchers (SAF)
    val sdf = remember { SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()) }
    val fileStamp = remember { sdf.format(Date()) }

    var shareUri by remember { mutableStateOf<Uri?>(null) }
    var shareMime by remember { mutableStateOf<String?>(null) }

    val createCsv = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val ok = writeCsv(context, uri, last7Days, categoryTotals)
                if (ok) {
                    shareUri = uri
                    shareMime = "text/csv"
                    snackbar.show("CSV exported")
                } else snackbar.show("Failed to export CSV")
            }
        }
    }

    val createPdf = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val ok = writeMockPdf(context, uri, last7Days, categoryTotals, last7Sum)
                if (ok) {
                    shareUri = uri
                    shareMime = "application/pdf"
                    snackbar.show("PDF exported (mock)")
                } else snackbar.show("Failed to export PDF")
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Expense Report") }) },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = NavRoutes.REPORT,
                onNavItemSelected = { route ->
                    if (route != NavRoutes.REPORT) {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbar) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Export + Share
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { createCsv.launch("expense_report_$fileStamp.csv") }) {
                    Icon(Icons.Default.FileDownload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Export CSV")
                }
                OutlinedButton(onClick = { createPdf.launch("expense_report_$fileStamp.pdf") }) {
                    Icon(Icons.Default.FileDownload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Export PDF (Mock)")
                }
                if (shareUri != null && shareMime != null) {
                    OutlinedButton(
                        onClick = { shareFile(context, shareUri!!, shareMime!!) }
                    ) {
                        Icon(Icons.Default.IosShare, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Share")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Last 7 days total: ₹$last7Sum", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            Text("Category-wise totals (selected day)", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            CategoryBarChart(categoryTotals = categoryTotals)
            Spacer(Modifier.height(8.dp))
            CategoryTotalsList(categoryTotals)

            Spacer(Modifier.height(24.dp))

            Text("Daily totals (last 7 days)", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Last7DaysChart(last7Days = last7Days)
            Spacer(Modifier.height(8.dp))
            Last7DaysList(last7Days)
        }
    }
}

