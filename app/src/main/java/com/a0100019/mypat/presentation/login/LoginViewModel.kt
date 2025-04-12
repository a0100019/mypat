package com.a0100019.mypat.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.tasks.await
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container: Container<LoginState, LoginSideEffect> = container(
        initialState = LoginState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(LoginSideEffect.Toast(message = throwable.message.orEmpty()))
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
        val userDataList = userDao.getAllUserData()
        val loginState = userDataList.find { it.id == "auth" }!!.value

        if(loginState == "0") {
            reduce {
                state.copy(
                    loginState = "unLogin"
                )
            }
        } else {
            reduce {
                state.copy(
                    loginState = "login"
                )
            }
        }

    }

    fun dialogChangeClick(string: String) = intent {
        reduce {
            state.copy(
                dialogState = string
            )
        }
    }

    fun onGuestLoginClick() = intent {
        userDao.update(id = "auth", value = "guest")
        postSideEffect(LoginSideEffect.NavigateToMainScreen)
        reduce {
            state.copy(
                dialogState = ""
            )
        }
    }

    fun onGoogleLoginClick(idToken: String) = intent {
        Log.e("login", "idToken = $idToken") // 🔍 여기 추가

        if (state.isLoggingIn) return@intent // 이미 로그인 중이면 리턴

        reduce { state.copy(isLoggingIn = true) }

        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            val user = authResult.user

            Log.e("login", "user = $user")

            user?.let {
                Log.e("login", "뷰모델 로그인 성공")
                userDao.update(id = "auth", value = it.uid)
                postSideEffect(LoginSideEffect.Toast("로그인 성공"))
                postSideEffect(LoginSideEffect.NavigateToMainScreen)
            }
        } catch (e: Exception) {
            Log.e("login", "뷰모델 로그인 실패", e)
            postSideEffect(LoginSideEffect.Toast("로그인 실패: ${e.localizedMessage}"))
        } finally {
            reduce { state.copy(isLoggingIn = false) }
        }
    }

    fun onNavigateToMainScreen() = intent {
        postSideEffect(LoginSideEffect.NavigateToMainScreen)
    }



}




@Immutable
data class LoginState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList(),
    val isLoggingIn:Boolean = false,
    val dialogState: String = "",
    val loginState: String = ""
)


//상태와 관련없는 것
sealed interface LoginSideEffect{
    class Toast(val message:String): LoginSideEffect
    data object NavigateToMainScreen: LoginSideEffect

}