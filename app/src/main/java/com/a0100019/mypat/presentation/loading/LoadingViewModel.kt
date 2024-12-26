package com.a0100019.mypat.presentation.loading

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.Todo
import com.a0100019.mypat.data.room.TodoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(

) : ViewModel(), ContainerHost<LoadingState, LoadingSideEffect> {

    override val container: Container<LoadingState, LoadingSideEffect> = container(
        initialState = LoadingState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(LoadingSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

//    // 뷰 모델 초기화 시 모든 Todo 데이터를 로드
//    init {
//        loadTodos()
//    }
//
//    //room에서 데이터 가져옴
//    private fun loadTodos() = intent {
//        // Flow 데이터를 State로 업데이트
//        todoDao.getAllTodos().collect { todos ->
//            reduce {
//                state.copy(todoList = todos)
//            }
//        }
//    }
//
//    fun onDailyNavigateClick() = intent {
//        postSideEffect(MainSideEffect.NavigateToDailyActivity)
//    }


}

@Immutable
data class LoadingState(
    val id:String = "",
    val password:String = "",
    val todoList: List<Todo> = emptyList()
)


//상태와 관련없는 것
sealed interface LoadingSideEffect{
    class Toast(val message:String): LoadingSideEffect
    data object NavigateToDailyActivity: LoadingSideEffect

}