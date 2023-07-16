package com.example.todoapp_clone.data

import kotlinx.coroutines.flow.Flow

class DefaultTasksRepository : TaskRepository {
    override fun getTasksStream(): Flow<List<Task>> {
        TODO("Not yet implemented")
    }
}