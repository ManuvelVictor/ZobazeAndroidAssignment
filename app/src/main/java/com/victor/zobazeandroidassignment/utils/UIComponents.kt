package com.victor.zobazeandroidassignment.utils

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.victor.zobazeandroidassignment.R
import com.victor.zobazeandroidassignment.navigation.NavItem
import com.victor.zobazeandroidassignment.navigation.NavRoutes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavItemSelected: (String) -> Unit
) {
    val items = listOf(
        NavItem("Add Expense", NavRoutes.ENTRY, Icons.Default.AttachMoney),
        NavItem("Expense List", NavRoutes.LIST, Icons.AutoMirrored.Filled.List),
        NavItem("Report", NavRoutes.REPORT, Icons.Default.Report)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavItemSelected(item.route) }
            )
        }
    }
}

@Composable
fun CategoryBarChart(categoryTotals: Map<String, Double>) {
    if (categoryTotals.isEmpty()) {
        Text("No data for selected day")
        return
    }
    val maxVal = categoryTotals.values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        categoryTotals.forEach { (cat, amt) ->
            Column {
                Text("$cat: ₹$amt", style = MaterialTheme.typography.bodyMedium)
                Box(Modifier.fillMaxWidth().height(8.dp)) {
                    val fraction = (amt / maxVal).toFloat().coerceIn(0f, 1f)
                    Box(
                        Modifier
                            .fillMaxWidth(fraction)
                            .height(8.dp)
                            .padding(end = 4.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
                    )
                }
            }
        }
    }
}

@Composable
fun Last7DaysChart(last7Days: List<Pair<Long, Double>>) {
    if (last7Days.isEmpty()) {
        Text("No data in the last 7 days")
        return
    }
    val maxVal = (last7Days.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)

    val barColor = MaterialTheme.colorScheme.secondary
    val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    Box(Modifier.fillMaxWidth().height(160.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val count = last7Days.size
            val gap = width / (count * 2f + (count + 1)) // compute gaps
            val barWidth = gap * 2f

            drawLine(
                color = axisColor,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 2f
            )
            drawLine(
                color = axisColor,
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = 2f
            )

            last7Days.forEachIndexed { index, pair ->
                val value = pair.second
                val hFrac = (value / maxVal).toFloat().coerceIn(0f, 1f)
                val barHeight = hFrac * (height - 8.dp.toPx())
                val x = gap + index * (barWidth + gap)
                val y = height - barHeight
                drawRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Text(
        "Recent days (left→oldest, right→today)",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}



@Composable
fun CategoryTotalsList(data: Map<String, Double>) {
    if (data.isEmpty()) {
        Text("No data", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        data.forEach { (cat, total) ->
            Text("- $cat: ₹$total", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun Last7DaysList(data: List<Pair<Long, Double>>) {
    if (data.isEmpty()) {
        Text("No data", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    val fmt = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        data.forEach { (day, total) ->
            Text("- ${fmt.format(Date(day))}: ₹$total", style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun shareFile(context: android.content.Context, uri: Uri, mime: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mime
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share report"))
}

fun writeCsv(
    context: android.content.Context,
    uri: Uri,
    last7Days: List<Pair<Long, Double>>,
    categoryTotals: Map<String, Double>
): Boolean {
    return try {
        context.contentResolver.openOutputStream(uri, "w").use { out ->
            if (out != null) {
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val b = StringBuilder()
                b.appendLine("Expense Report")
                b.appendLine()
                b.appendLine("Last 7 Days")
                b.appendLine("Date,Total")
                last7Days.forEach { (d, t) -> b.appendLine("${df.format(Date(d))},$t") }
                b.appendLine()
                b.appendLine("Category Totals (Selected Day)")
                b.appendLine("Category,Total")
                categoryTotals.forEach { (c, t) -> b.appendLine("$c,$t") }
                out.write(b.toString().toByteArray())
                out.flush()
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// Mock PDF writer (plain text into .pdf for demo)
fun writeMockPdf(
    context: android.content.Context,
    uri: Uri,
    last7Days: List<Pair<Long, Double>>,
    categoryTotals: Map<String, Double>,
    last7Sum: Double
): Boolean {
    return try {
        context.contentResolver.openOutputStream(uri, "w").use { out ->
            if (out != null) {
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val b = StringBuilder()
                b.append("Expense Report\n\n")
                b.append("Last 7 Days Total: ₹$last7Sum\n\n")
                b.append("Daily Totals\n")
                last7Days.forEach { (d, t) -> b.append("- ${df.format(Date(d))}: ₹$t\n") }
                b.append("\nCategory Totals (Selected Day)\n")
                categoryTotals.forEach { (c, t) -> b.append("- $c: ₹$t\n") }
                out.write(b.toString().toByteArray())
                out.flush()
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@Composable
fun ExpenseCard(
    title: String,
    amount: Double,
    subtitleTop: String,
    subtitleBottom: String?
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("$title - ₹$amount", style = MaterialTheme.typography.titleMedium)
            Text(subtitleTop, style = MaterialTheme.typography.bodySmall)
            subtitleBottom?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSection(
    initial: Long,
    onConfirm: (Long) -> Unit,
    onCancel: () -> Unit
) {
// Material3 DatePicker
    val state = rememberDatePickerState(initialSelectedDateMillis = initial)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("Select Date", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        DatePicker(state = state)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val ms = state.selectedDateMillis ?: System.currentTimeMillis()
                    onConfirm(ms)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Apply")
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun EmptyLottie() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.no_data
        )
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(220.dp)
    )
}
