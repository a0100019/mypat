package com.a0100019.mypat.presentation.setting

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.presentation.daily.walk.StepCounterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<SettingState, SettingSideEffect> {

    override val container: Container<SettingState, SettingSideEffect> = container(
        initialState = SettingState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(SettingSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
    }

    //room에서 데이터 가져옴
    private fun loadData() = intent {


    }

    fun onCloseClick() = intent {

    }

    fun onTermsClick() = intent {
        postSideEffect(SettingSideEffect.OpenChromeTab("https://www.notion.so/1d13b2c14dcb806fb9a4edcaac84fb28?pvs=4"))
    }



}



@Immutable
data class SettingState(
    val userData: List<User> = emptyList()
)


sealed interface SettingSideEffect {
    class Toast(val message: String) : SettingSideEffect
    data class OpenChromeTab(val url: String) : SettingSideEffect
}
