package com.example.todoapp_clone.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoapp_clone.data.Task

@Database(entities = [LocalTask::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

@Database(entities = [LocalTask::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}