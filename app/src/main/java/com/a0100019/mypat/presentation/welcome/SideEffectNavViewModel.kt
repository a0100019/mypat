package com.a0100019.mypat.presentation.welcome

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class SideEffectNavViewModel @Inject constructor(
) : ViewModel(), ContainerHost<SideEffectNavState, SideEffectNavSideEffect> {

    //아래의 부분이 꼭 있어야하는지 확인!!!!!!!!!
    override val container: Container<SideEffectNavState, SideEffectNavSideEffect> = container(
        initialState = SideEffectNavState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent { postSideEffect(SideEffectNavSideEffect.Toast(throwable.message.orEmpty())) }
            }
        }
    )


    // 화면전환 할지 말지 정하는 코드 작성!!!!!!!!
    fun onMoveSelectClick() = intent{
        if(state.password != state.repeatPassword){
            reduce {
                state.copy(password = "11")
            }
            postSideEffect(SideEffectNavSideEffect.Toast(message = "한번 더 눌러주세요."))
            return@intent
        } else {
            postSideEffect(SideEffectNavSideEffect.NavigateToSelectScreen)
            postSideEffect(SideEffectNavSideEffect.Toast(message = "이동 완료"))

        }

        // state 한번에 바꾸는 법
//    reduce {
//        SideEffectNavState(
//            id = "newId",
//            username = "newUsername",
//            password = "새로운 비밀번호",
//            repeatPassword = "새로운 반복 비밀번호"
//        )
//    }

    }

}

@Immutable
data class SideEffectNavState(
    val id: String = "",
    val username: String = "",
    val password: String = "1",
    val repeatPassword: String = "11",
)

sealed interface SideEffectNavSideEffect {
    class Toast(val message: String) : SideEffectNavSideEffect

    object NavigateToSelectScreen : SideEffectNavSideEffect
}