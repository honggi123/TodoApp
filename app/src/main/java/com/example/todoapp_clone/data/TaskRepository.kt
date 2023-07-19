package com.example.todoapp_clone.data

import com.example.todoapp_clone.data.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTaskStream(taskId: String): Flow<Task?>

    suspend fun refreshTask(taskId: String)

    fun getTasksStream(): Flow<List<Task>>

    suspend fun refresh()

    suspend fun completeTask(taskId: String)

    suspend fun updateTask(taskId: String, title: String, description: String)

    suspend fun activateTask(taskId: String)

    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Task?

    suspend fun deleteTask(taskId: String)

    suspend fun createTask(title: String, description: String): String

}