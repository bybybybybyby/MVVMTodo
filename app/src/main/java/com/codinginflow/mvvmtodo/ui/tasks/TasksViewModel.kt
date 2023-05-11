package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

// TODO: @ViewModelInject is deprecated; Now use @HiltViewModel
class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    // Use `combine`, which is part of Flow library, to combine multiple Flows.
    // If any 1 value changes, get back latest values of all 3 values
    private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ) {
        query, sortOrder, hideCompleted ->
        Triple(query, sortOrder, hideCompleted)  // Kotlin can only return a single value, so use Triple to wrap them up
    }
        .flatMapLatest { (query, sortOrder, hideCompleted) ->   // Destructuring here for ease of use
        taskDao.getTasks(query, sortOrder, hideCompleted)
    }

    // Common practice to use Flow below the ViewModel, then transform to LiveData inside ViewModel so we can observe the LiveData in the Fragment.
    // Flow contains the whole stream of data, while LiveData is just the latest value. (The app would be able to still work using either Flow or LiveData from Room all the way to the fragment, but this is common pattern to use)
    // Flow is more flexible; has different operators to transform data; can switch thread in the Flow; don't lose data since it's a stream.
    // LiveData has a benefit that makes handling the lifecycle of fragment easier because it is lifecycle aware (eg: if fragment goes in background and becomes inactive, LiveData would detect and stop dispatching events)
    val tasks = tasksFlow.asLiveData()

}

enum class SortOrder { BY_NAME, BY_DATE }