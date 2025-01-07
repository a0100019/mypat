package com.a0100019.mypat.presentation.main.world.patDialog

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.UserDao
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
class PatDialogViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<PatDialogState, PatDialogSideEffect> {

    override val container: Container<PatDialogState, PatDialogSideEffect> = container(
        initialState = PatDialogState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(PatDialogSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadUserData()
    }

    //room에서 데이터 가져옴
    private fun loadUserData() = intent {
        // Flow 데이터를 State로 업데이트


    }



    fun dialogPatIdChange(clickId : String) = intent {
        reduce {
            state.copy(dialogPatId = clickId)
        }
    }


}

@Immutable
data class PatDialogState(
    val dialogPatId : String = "0"
)

