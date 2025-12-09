package com.a0100019.mypat.presentation.daily


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.PowerManager
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

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun walkPermissionCheck(context: Context) = intent {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // 권한 있을 때 처리
            notificationPermissionCheck(context)
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun notificationPermissionCheck(context: Context) = intent {

        val permission = Manifest.permission.POST_NOTIFICATIONS

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // 권한 있음 → 정상 진행
            batteryPermissionCheck(context)
        } else {
            val activity = context as? Activity
            val isDeniedPermanently = activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false

            if (isDeniedPermanently) {
                // 🔥 완전 거절 → 설정 화면으로 유도
                reduce {
                    state.copy(
                        situation = "notificationPermissionSetting"
                    )
                }
            } else {
                // 📌 단순 거절 → 다시 요청 가능
                reduce {
                    state.copy(
                        situation = "notificationPermissionRequest"
                    )
                }
            }
        }
    }

    private fun batteryPermissionCheck(context: Context) = intent {

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val isIgnoring = pm.isIgnoringBatteryOptimizations(context.packageName)

        if (isIgnoring) {
            postSideEffect(DailySideEffect.NavigateToWalkScreen)
        } else {
            reduce {
                state.copy(situation = "batteryPermissionRequest")
            }
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun onDialogPermissionCheckClick(context: Context) = intent {

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // 권한 있을 때 처리
            notificationPermissionCheck(context)
        } else {
            reduce {
                state.copy(
                    situation = "walkPermissionSettingNo"
                )
            }
        }

    }

    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun onDialogNotificationPermissionCheckClick(context: Context) = intent {

        val permission = Manifest.permission.POST_NOTIFICATIONS

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // 권한 있을 때 처리
            batteryPermissionCheck(context)
        } else {
            reduce {
                state.copy(
                    situation = "notificationPermissionSettingNo"
                )
            }
        }

    }

    fun onDialogBatteryOptimizationPermissionCheckClick(context: Context) = intent {

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val isIgnoring = pm.isIgnoringBatteryOptimizations(context.packageName)

        if (isIgnoring) {
            // ✅ 배터리 최적화 예외 허용됨
            reduce {
                state.copy(
                    situation = ""
                )
            }
            postSideEffect(DailySideEffect.NavigateToWalkScreen)

        } else {
            // ❌ 아직 허용 안 됨
            reduce {
                state.copy(
                    situation = "batteryPermissionSettingNo"
                )
            }
        }
    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                situation = ""
            )
        }
    }


}

@Immutable
data class DailyState(
    val userData: List<User> = emptyList(),
    val situation: String = ""
)


//상태와 관련없는 것
sealed interface DailySideEffect{
    class Toast(val message:String): DailySideEffect
    data object NavigateToWalkScreen : DailySideEffect

}