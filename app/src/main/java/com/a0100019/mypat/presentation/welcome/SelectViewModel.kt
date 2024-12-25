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
class SelectViewModel @Inject constructor(

) : ViewModel(), ContainerHost<SelectState, SelectSideEffect> {

    override val container: Container<SelectState, SelectSideEffect> = container(
        initialState = SelectState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(SelectSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    fun onCalculatorClick() = intent {

        postSideEffect(SelectSideEffect.Toast(message = "계산기 이동"))

        //엑티비티 간 전환은 뷰모델에선 이것만 적고 Screen에서 처리
        postSideEffect(SelectSideEffect.NavigateToMainActivity)

    }


}

@Immutable
data class SelectState(
    val id:String = "",
    val password:String = "",
)


//상태와 관련없는 것
sealed interface SelectSideEffect{
    class Toast(val message:String):SelectSideEffect
    object NavigateToMainActivity:SelectSideEffect
}