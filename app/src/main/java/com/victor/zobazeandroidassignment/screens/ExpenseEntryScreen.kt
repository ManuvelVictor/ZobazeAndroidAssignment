package com.victor.zobazeandroidassignment.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.victor.zobazeandroidassignment.data.model.Expense
import com.victor.zobazeandroidassignment.navigation.NavRoutes
import com.victor.zobazeandroidassignment.utils.BottomNavigationBar
import com.victor.zobazeandroidassignment.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val todayTotal by viewModel.totalForDateFlow.collectAsState()

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

// Category sheet (single select from mocked list)
    val categories = listOf("Staff", "Travel", "Food", "Utility")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var showCategorySheet by remember { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

// Receipt picker using SAF
    var receiptUri by remember { mutableStateOf<Uri?>(null) }
    val receiptPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            // Persist permission if needed outside this process lifetime; for now just store URI
            receiptUri = uri
        }
    )

// Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

// Small submission animation (scale) to give subtle feedback
    var submitted by remember { mutableStateOf(false) }
    val submitScale by animateFloatAsState(if (submitted) 0.98f else 1f, label = "submitScale")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Expense") }) },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = NavRoutes.ENTRY,
                onNavItemSelected = { route ->
                    if (route != NavRoutes.ENTRY) {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Real-time total
            Text(
                text = "Total Spent Today: ₹$todayTotal",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { new ->
                    // allow only digits and dot
                    if (new.isEmpty() || new.matches(Regex("""\d*\.?\d{0,2}"""))) {
                        amount = new
                    }
                },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Category (opens bottom sheet)
            OutlinedButton(
                onClick = { showCategorySheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Category, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Category: $selectedCategory")
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }

            Spacer(Modifier.height(16.dp))

            // Notes (max 100 chars)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it.take(100) },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Receipt picker
            OutlinedButton(
                onClick = {
                    // Allow images and PDFs as an example; tweak as needed
                    receiptPicker.launch(arrayOf("image/*", "application/pdf"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = "Pick Receipt")
                Spacer(Modifier.width(8.dp))
                Text(if (receiptUri == null) "Pick Receipt" else "Change Receipt")
            }

            if (receiptUri != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Attached: ${receiptUri.toString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            // Submit
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0 && selectedCategory.isNotBlank()) {
                        submitted = true
                        scope.launch {
                            // Slight delay to let animation play
                            viewModel.addExpense(
                                Expense(
                                    title = title.trim(),
                                    amount = amt,
                                    category = selectedCategory,
                                    notes = notes.ifBlank { null },
                                    receiptImagePath = receiptUri?.toString()
                                )
                            )
                            snackbarHostState.showSnackbar("Expense added")
                            // Reset inputs
                            title = ""
                            amount = ""
                            notes = ""
                            // Keep selected category and receipt if you want; here we clear receipt
                            receiptUri = null
                            submitted = false
                        }
                    } else {
                        scope.launch {
                            val msg = when {
                                title.isBlank() -> "Please enter a title"
                                amt <= 0.0 -> "Please enter a valid amount"
                                selectedCategory.isBlank() -> "Please choose a category"
                                else -> "Invalid input"
                            }
                            snackbarHostState.showSnackbar(msg)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(submitScale)
            ) {
                Icon(imageVector = Icons.Default.AttachMoney, contentDescription = "Submit")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit")
            }
        }
    }

// Category bottom sheet (single-select, scrollable)
    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = categorySheetState
        ) {
            // Make it scrollable with a LazyColumn
            Column(Modifier.fillMaxWidth()) {
                Text(
                    "Choose Category",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                // RadioList pattern
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(categories.size) { idx ->
                        val cat = categories[idx]
                        ListItem(
                            headlineContent = { Text(cat) },
                            trailingContent = {
                                RadioButton(
                                    selected = selectedCategory == cat,
                                    onClick = {
                                        selectedCategory = cat
                                        showCategorySheet = false
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}