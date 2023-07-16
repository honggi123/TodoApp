package com.example.todoapp_clone.data

import com.example.todoapp_clone.data.Task
import com.example.todoapp_clone.data.source.local.LocalTask


// External to local
fun Task.toLocal() = LocalTask(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
)

// Local to External
fun LocalTask.toExternal() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
)