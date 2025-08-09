package com.victor.zobazeandroidassignment.data.repository

import com.victor.zobazeandroidassignment.data.dao.ExpenseDao
import com.victor.zobazeandroidassignment.data.model.Expense
import com.victor.zobazeandroidassignment.data.model.ExpenseEntity
import com.victor.zobazeandroidassignment.utils.Constants.DAY_MS
import com.victor.zobazeandroidassignment.utils.dayStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val dao: ExpenseDao
) {
    suspend fun addExpense(expense: Expense) {
        dao.insertExpense(expense.toEntity())
    }

    fun getExpensesByDate(date: Long): Flow<List<Expense>> {
        val start = dayStart(date)
        val end = start + DAY_MS - 1
        return dao.getExpensesBetweenDates(start, end)
            .map { list -> list.map { it.toDomain() } }
    }

    fun getExpensesBetween(startDate: Long, endDate: Long): Flow<List<Expense>> =
        dao.getExpensesBetweenDates(startDate, endDate).map { it.map { e -> e.toDomain() } }

    private fun Expense.toEntity() = ExpenseEntity(
        id = id,
        title = title,
        amount = amount,
        category = category,
        notes = notes,
        date = date,
        receiptImagePath = receiptImagePath
    )

    private fun ExpenseEntity.toDomain() = Expense(
        id = id,
        title = title,
        amount = amount,
        category = category,
        notes = notes,
        date = date,
        receiptImagePath = receiptImagePath
    )
}