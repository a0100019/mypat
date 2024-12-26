package com.a0100019.mypat.presentation.welcome


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : ViewModel(), ContainerHost<MainState, MainSideEffect> {

    override val container: Container<MainState, MainSideEffect> = container(
        initialState = MainState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(MainSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    fun onDailyNavigateClick() = intent {
        postSideEffect(MainSideEffect.NavigateToDailyActivity)
    }

    fun onStoreNavigateClick() = intent {
        postSideEffect(MainSideEffect.NavigateToStoreActivity)
    }

    fun onGameNavigateClick() = intent {
        postSideEffect(MainSideEffect.NavigateToGameActivity)
    }

    fun onIndexNavigateClick() = intent {
        postSideEffect(MainSideEffect.NavigateToIndexActivity)
    }



}

@Immutable
data class MainState(
    val id:String = "",
    val password:String = "",
)


//상태와 관련없는 것
sealed interface MainSideEffect{
    class Toast(val message:String):MainSideEffect
    data object NavigateToDailyActivity:MainSideEffect
    data object NavigateToStoreActivity:MainSideEffect
    data object NavigateToGameActivity:MainSideEffect
    data object NavigateToIndexActivity:MainSideEffect

}