package com.victor.zobazeandroidassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor.zobazeandroidassignment.data.model.Expense
import com.victor.zobazeandroidassignment.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repo: ExpenseRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(currentDayMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val expensesFlow: StateFlow<List<Expense>> =
        selectedDate
            .flatMapLatest { date -> repo.getExpensesByDate(date) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalForDateFlow: StateFlow<Double> =
        selectedDate
            .flatMapLatest { date ->
                repo.getExpensesByDate(date).map { list -> list.sumOf { it.amount } }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    // Category totals for selected date
    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryTotalsFlow: StateFlow<Map<String, Double>> =
        expensesFlow
            .map { list -> list.groupBy({ it.category }, { it.amount }).mapValues { it.value.sum() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    // Last 7 days totals (from selectedDate going backwards 6 days)
    @OptIn(ExperimentalCoroutinesApi::class)
    val last7DaysFlow: StateFlow<List<Pair<Long, Double>>> =
        selectedDate.flatMapLatest { anchor ->
            // Build 7 days window [anchor-6d, anchor]
            val end = dayStart(anchor)
            val start = end - 6L * DAY_MS
            repo.getExpensesBetween(start, end + DAY_MS - 1)
                .map { all ->
                    // Aggregate by day
                    val byDay = all.groupBy { dayStart(it.date) }
                        .mapValues { entry -> entry.value.sumOf { it.amount } }
                    // Ensure all 7 days are present
                    (0..6).map { i ->
                        val day = end - (6 - i) * DAY_MS
                        day to (byDay[day] ?: 0.0)
                    }
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val today: Long get() = currentDayMillis()

    fun setDate(date: Long) {
        _selectedDate.value = date
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repo.addExpense(expense)
        }
    }

    private fun currentDayMillis(): Long = System.currentTimeMillis()

    private fun dayStart(ms: Long): Long {
        val oneDay = DAY_MS
        return ms - (ms % oneDay)
    }

    companion object {
        private const val DAY_MS = 24L * 60L * 60L * 1000L
    }
}