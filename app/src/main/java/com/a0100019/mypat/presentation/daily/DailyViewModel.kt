package com.a0100019.mypat.presentation.daily


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.setting.SettingSideEffect
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

        val userDataList = userDao.getAllUserData()

        reduce {
            state.copy(
                userData = userDataList
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun walkPermissionCheck(context: Context) = intent {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // 권한 있을 때 처리
            postSideEffect(DailySideEffect.NavigateToWalkScreen)
        } else {
            val activity = context as? Activity
            val isDeniedPermanently = activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACTIVITY_RECOGNITION)
            } ?: false

            if (isDeniedPermanently) {
                // 완전 거부했을 때 처리 (설정으로 유도 등)
                reduce {
                    state.copy(
                        situation = "walkPermissionSetting"
                    )
                }
            } else {
                // 단순 거부했을 때 처리 (권한 요청 UI 다시 띄울 수 있음)
                reduce {
                    state.copy(
                        situation = "walkPermissionRequest"
                    )
                }
            }
        }
    }


}

@Immutable
data class DailyState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList(),
    val situation: String = ""
)


//상태와 관련없는 것
sealed interface DailySideEffect{
    class Toast(val message:String): DailySideEffect
    data object NavigateToWalkScreen : DailySideEffect

}