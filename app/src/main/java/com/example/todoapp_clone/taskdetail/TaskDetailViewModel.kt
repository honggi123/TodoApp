package com.example.todoapp_clone.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp_clone.R
import com.example.todoapp_clone.TodoDestinationsArgs
import com.example.todoapp_clone.data.Task
import com.example.todoapp_clone.data.TaskRepository
import com.example.todoapp_clone.util.Async
import com.example.todoapp_clone.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isTaskDeleted: Boolean = false
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val taskId: String = savedStateHandle[TodoDestinationsArgs.TASK_ID_ARG]!!

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isTaskDeleted = MutableStateFlow(false)
    private val _taskAsync = taskRepository.getTaskStream(taskId)
        .map { handleTask(it) }
        .catch { emit(Async.Error(R.string.loading_task_error)) }

    val uiState: StateFlow<TaskDetailUiState> = combine(
        _userMessage, _isLoading, _isTaskDeleted, _taskAsync
    ) { userMessage, isLoading, isTaskDeleted, taskAsync ->
        when (taskAsync) {
            Async.Loading -> {
                TaskDetailUiState(isLoading = true)
            }
            is Async.Error -> {
                TaskDetailUiState(
                    userMessage = taskAsync.errorMessage,
                    isTaskDeleted = isTaskDeleted
                )
            }
            is Async.Success -> {
                TaskDetailUiState(
                    task = taskAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage,
                    isTaskDeleted = isTaskDeleted
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = TaskDetailUiState(isLoading = true)
        )

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            taskRepository.refreshTask(taskId)
            _isLoading.value = false
        }
    }

    fun deleteTask() = viewModelScope.launch {
        taskRepository.deleteTask(taskId)
        _isTaskDeleted.value = true
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val task = uiState.value.task ?: return@launch
        if (completed) {
            taskRepository.completeTask(task.id)
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            taskRepository.activateTask(task.id)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    private fun handleTask(task: Task?): Async<Task?> {
        if (task == null) {
            return Async.Error(R.string.task_not_found)
        }
        return Async.Success(task)
    }
}