package com.victor.zobazeandroidassignment.data.model

data class Expense(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String? = null,
    val date: Long = System.currentTimeMillis(),
    val receiptImagePath: String? = null
)

