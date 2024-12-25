package com.a0100019.mypat.presentation.main.second


import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.Todo
import com.a0100019.mypat.data.room.TodoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao
) : ViewModel(), ContainerHost<TodoState, TodoSideEffect> { // SideEffect를 Nothing으로 지정, 사이드 안 쓸 때!!

    override val container: Container<TodoState, TodoSideEffect> =
        container(
            initialState = TodoState(),
            buildSettings = {
                this.exceptionHandler = CoroutineExceptionHandler {_, throwable ->
                    intent { postSideEffect(TodoSideEffect.Toast(throwable.message.orEmpty())) }
                }
            }
        )

    // 초기화 시 모든 Todo 데이터를 로드
    init {
        loadTodos()
    }

    //room에서 데이터 가져옴
    private fun loadTodos() = intent {
        // Flow 데이터를 State로 업데이트
        todoDao.getAllTodos().collect { todos ->
            reduce {
                state.copy(todoList = todos)
            }
        }
    }

    //아이디 입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onTodoTextChange(todoText: String) = blockingIntent {
        reduce {
            state.copy(todoText = todoText)
        }
    }

    fun addTodo() = intent {
        val newTodo = Todo(title = state.todoText)
        todoDao.insert(newTodo)
        // 새로 추가된 Todo를 State에 반영
        reduce {
            state.copy(todoList = state.todoList + newTodo)
            state.copy(todoText = "")
        }
    }

    fun deleteTodo() = intent {
        //지우는건 primaryKey만 일치하면 뒤는 다 틀려도 지움
        val deleteTodo = Todo(id = state.todoText.toInt(), title = "기본값없어서 적어줌")
        todoDao.delete(deleteTodo)
        reduce {
            state.copy(todoList = state.todoList - deleteTodo)
            state.copy(todoText = "")
        }
        postSideEffect(TodoSideEffect.Toast(message = "Todo 삭제 완료!"))
    }

    fun updateTodo() = intent {
        val updatedTodo = Todo(id = state.todoText.toInt(), title = "300", isDone = true)
        todoDao.updateTodoById(id = updatedTodo.id, title = updatedTodo.title, isDone = updatedTodo.isDone)
        reduce {
            state.copy(
                todoList = state.todoList.map {
                    if (it.id == updatedTodo.id) updatedTodo else it
                }
            )
            state.copy(todoText = "")
        }
    }

}

// ViewModel에서 관리할 상태
@Immutable
data class TodoState(
    val todoText: String = "",
    val todoList: List<Todo> = emptyList()
)

// 단발성 이벤트 정의
sealed interface TodoSideEffect {
    data class Toast(val message: String) : TodoSideEffect
}
