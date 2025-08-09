package com.victor.zobazeandroidassignment.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String? = null,
    val date: Long,
    val receiptImagePath: String? = null
)
