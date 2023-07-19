package com.example.todoapp_clone.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp_clone.R
import com.example.todoapp_clone.TodoDestinationsArgs
import com.example.todoapp_clone.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.RuntimeException

data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val isTaskCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isTaskSaved: Boolean = false
)

class AddEditTaskViewModel constructor(
    private val taksRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: String? = savedStateHandle[TodoDestinationsArgs.TASK_ID_ARG]

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    fun saveTask() {
        if (uiState.value.title.isEmpty() || uiState.value.description.isEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.empty_task_message)
            }
            return
        }

        if(taskId == null) {
            createNewTask()
        } else {
            updateTask()
        }
    }

    private fun updateTask() {
        if (taskId == null) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        viewModelScope.launch {
            taksRepository.updateTask(
                taskId,
                title = uiState.value.title,
                description = uiState.value.description,
            )
            _uiState.update {
                it.copy(isTaskSaved = true)
            }
        }
    }

    private fun createNewTask() = viewModelScope.launch {
        taksRepository.createTask(uiState.value.title, uiState.value.description)
        _uiState.update {
            it.copy(isTaskSaved = true)
        }
    }


    private fun loadTask(taskId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            taksRepository.getTask(taskId).let { task ->
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            isTaskCompleted = task.isCompleted,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun updateTitle(newTitle: String) {
        if (taskId == null) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        viewModelScope.launch {
            taksRepository
            _uiState.update {
                it.copy(title = newTitle)
            }
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(description = newDescription)
        }
    }
}