package com.example.todoapp_clone.data.source.network

interface NetworkDataSource {

    suspend fun loadTasks(): List<NetworkTask>

    suspend fun saveTasks(tasks: List<NetworkTask>)
}
