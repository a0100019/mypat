package com.a0100019.mypat.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert
    suspend fun insert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    //Flow는 이미 계속 비동기로 상태를 관찰하기 때문에 비동기함수인 suspend를 붙히면 안됨
    @Query("""
        SELECT * 
        FROM todo_table 
        ORDER BY id DESC
        """)
    fun getAllTodos(): Flow<List<Todo>>

    @Query("""
        UPDATE todo_table 
        SET title = :title, isDone = :isDone 
        WHERE id = :id
        """)
    suspend fun updateTodoById(id: Int, title: String, isDone: Boolean)


}