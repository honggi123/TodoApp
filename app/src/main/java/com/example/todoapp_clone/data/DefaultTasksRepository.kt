package com.example.todoapp_clone.data

import com.example.todoapp_clone.data.source.local.TaskDao
import com.example.todoapp_clone.data.source.network.NetworkDataSource
import com.example.todoapp_clone.di.ApplicationScope
import com.example.todoapp_clone.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTasksRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: TaskDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : TaskRepository {

    override suspend fun createTask(title: String, description: String): String {
        var taskId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val task = Task(
            title = title,
            description = description,
            id = taskId
        )
        localDataSource.upsert(task.toLocal())
        saveTasksToNetwork()
        return taskId
    }

    override fun getTaskStream(taskId: String): Flow<Task?> {
        return localDataSource.observeById(taskId).map { it.toExternal() }
    }

    override suspend fun refreshTask(taskId: String) {
        refresh()
    }

    override fun getTasksStream(): Flow<List<Task>> {
        return localDataSource.observeAll().map { tasks ->
            tasks.toExternal()
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Task? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(taskId)?.toExternal()
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteTasks = networkDataSource.loadTasks()
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteTasks.toLocal())
        }
    }

    override suspend fun completeTask(taskId: String) {
        localDataSource.updateCompleted(taskId = taskId, completed = true)
        saveTasksToNetwork()
    }

    override suspend fun updateTask(taskId: String, title: String, description: String) {
        val task = getTask(taskId)?.copy(
            title = title,
            description = description
        ) ?: throw Exception("Task (id $taskId) not found")

        localDataSource.upsert(task.toLocal())
        saveTasksToNetwork()
    }

    override suspend fun activateTask(taskId: String) {
        localDataSource.updateCompleted(taskId = taskId, completed = false)
        saveTasksToNetwork()
    }

    override suspend fun deleteTask(taskId: String) {
        localDataSource.deleteById(taskId)
        saveTasksToNetwork()
    }

    private fun saveTasksToNetwork() {
        scope.launch {
            try {
                val localTasks = localDataSource.getAll()
                val networkTasks = withContext(dispatcher) {
                    localTasks.toNetwork()
                }
                networkDataSource.saveTasks(networkTasks)
            } catch (e: Exception) {
                // Todo
            }
        }
    }
}