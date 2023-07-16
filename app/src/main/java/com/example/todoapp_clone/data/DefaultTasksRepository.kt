package com.example.todoapp_clone.data

import com.example.todoapp_clone.data.source.local.TaskDao
import com.example.todoapp_clone.data.source.network.NetworkDataSource
import com.example.todoapp_clone.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTasksRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: TaskDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : TaskRepository {
    override fun getTasksStream(): Flow<List<Task>> {
       return localDataSource.observeAll().map { tasks ->
           tasks.toExternal()
       }
    }
}