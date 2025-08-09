package com.victor.zobazeandroidassignment.utils

import androidx.compose.material3.SnackbarHostState
import com.victor.zobazeandroidassignment.utils.Constants.DAY_MS

fun dayStart(ms: Long) = ms - (ms % DAY_MS)

suspend fun SnackbarHostState.show(message: String) {
    showSnackbar(message)
}