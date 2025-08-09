package com.victor.zobazeandroidassignment.di

import android.content.Context
import androidx.room.Room
import com.victor.zobazeandroidassignment.data.dao.ExpenseDao
import com.victor.zobazeandroidassignment.data.db.ExpenseDatabase
import com.victor.zobazeandroidassignment.data.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExpenseDatabase =
        Room.databaseBuilder(
            context,
            ExpenseDatabase::class.java,
            "expense-db"
        )
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun provideExpenseDao(db: ExpenseDatabase): ExpenseDao =
        db.expenseDao()

    @Provides
    @Singleton
    fun provideExpenseRepository(dao: ExpenseDao): ExpenseRepository =
        ExpenseRepository(dao)
}
