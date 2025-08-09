package com.victor.zobazeandroidassignment.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.victor.zobazeandroidassignment.data.dao.ExpenseDao
import com.victor.zobazeandroidassignment.data.model.ExpenseEntity

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
