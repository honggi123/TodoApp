package com.example.todoapp_clone.data

import com.example.todoapp_clone.data.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksStream(): Flow<List<Task>>
}