package com.codinginflow.mvvmtodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Flow represents a stream of data; whenever a change in database table,
    // Room puts a new list of tasks into the Flow
    // Flow can only be used or collected inside a coroutine; this is why no suspend keyword used;
    // all suspension happens inside the Flow; Flow is an asynchronous stream of data
    @Query("SELECT * FROM task_table")
    fun getTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun updated(task: Task)

    @Delete
    suspend fun delete(task: Task)
}