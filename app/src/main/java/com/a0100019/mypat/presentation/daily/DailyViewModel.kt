package com.a0100019.mypat.presentation.daily


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
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
class DailyViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<DailyState, DailySideEffect> {

    override val container: Container<DailyState, DailySideEffect> = container(
        initialState = DailyState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(DailySideEffect.Toast(message = throwable.message.orEmpty()))
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


    }



}

@Immutable
data class DailyState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList()
)


//상태와 관련없는 것
sealed interface DailySideEffect{
    class Toast(val message:String): DailySideEffect

}