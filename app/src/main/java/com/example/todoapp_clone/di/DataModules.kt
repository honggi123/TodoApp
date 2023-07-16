package com.example.todoapp_clone.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp_clone.data.DefaultTasksRepository
import com.example.todoapp_clone.data.TaskRepository
import com.example.todoapp_clone.data.source.local.ToDoDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule() {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultTasksRepository): TaskRepository
}


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModules {
    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): ToDoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java,
            "Tasks.db"
        ).build()
    }
}