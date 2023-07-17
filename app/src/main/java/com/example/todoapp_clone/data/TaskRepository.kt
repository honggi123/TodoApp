package com.example.todoapp_clone.data

import com.example.todoapp_clone.data.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksStream(): Flow<List<Task>>

    suspend fun refresh()

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(taskId: String)

}