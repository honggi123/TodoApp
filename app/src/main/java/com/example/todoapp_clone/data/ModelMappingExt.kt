package com.example.todoapp_clone.data

import com.example.todoapp_clone.data.Task
import com.example.todoapp_clone.data.source.local.LocalTask
import com.example.todoapp_clone.data.source.network.NetworkTask
import com.example.todoapp_clone.data.source.network.TaskStatus


// External to local
fun Task.toLocal() = LocalTask(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
)

// Network to Local
fun NetworkTask.toLocal() = LocalTask(
    id = id,
    title = title,
    description = shortDescription,
    isCompleted = (status == TaskStatus.COMPLETE),
)

@JvmName("networkToLocal")
fun List<NetworkTask>.toLocal() = map(NetworkTask::toLocal)

@JvmName("localToExternal")
fun List<LocalTask>.toExternal() = map(LocalTask::toExternal)

fun List<Task>.toLocal() = map(Task::toLocal)

fun LocalTask.toNetwork() = NetworkTask(
    id = id,
    title = title,
    shortDescription = description,
    status = if (isCompleted) { TaskStatus.COMPLETE } else { TaskStatus.ACTIVE }
)

fun List<LocalTask>.toNetwork() = map(LocalTask::toNetwork)

// Local to External
fun LocalTask.toExternal() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
)

