package com.codinginflow.mvvmtodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Can call this function from the outside passing in SortOrder to use on below queries
    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    // Flow represents a stream of data; whenever a change in database table,
    // Room puts a new list of tasks into the Flow
    // Flow can only be used or collected inside a coroutine; this is why no suspend keyword used;
    // all suspension happens inside the Flow; Flow is an asynchronous stream of data
    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun updated(task: Task)

    @Delete
    suspend fun delete(task: Task)
}