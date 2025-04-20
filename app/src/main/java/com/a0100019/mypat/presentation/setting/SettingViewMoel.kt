package com.a0100019.mypat.presentation.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.pet.PatDao
import com.a0100019.mypat.data.room.sudoku.SudokuDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.data.room.world.WorldDao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
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
class SettingViewModel @Inject constructor(
    private val userDao: UserDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val diaryDao: DiaryDao,
    private val englishDao: EnglishDao,
    private val koreanIdiomDao: KoreanIdiomDao,
    private val sudokuDao: SudokuDao,
    private val walkDao: WalkDao,
    private val worldDao: WorldDao
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
        val userDataList = userDao.getAllUserData()
        val googleLoginState = FirebaseAuth.getInstance().currentUser != null

        reduce {
            state.copy(
                userDataList = userDataList,
                googleLoginState = googleLoginState
            )
        }
    }

    fun onCloseClick() = intent {

    }

    fun onTermsClick() = intent {
//        postSideEffect(SettingSideEffect.OpenChromeTab("https://www.notion.so/1d13b2c14dcb806fb9a4edcaac84fb28?pvs=4"))
    }

    fun onSignOutClick() = intent {

        FirebaseAuth.getInstance().signOut()
        // 현재 사용자 null이면 로그아웃 성공
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // ✅ 로그아웃 성공
            // 사용자 데이터 삭제
            userDao.update(id = "auth", value = "0")
            postSideEffect(SettingSideEffect.Toast("로그아웃 되었습니다"))
            postSideEffect(SettingSideEffect.NavigateToLoginScreen)
        } else {
            // ❌ 로그아웃 실패
            postSideEffect(SettingSideEffect.Toast("로그아웃에 실패했습니다"))
        }

    }

    fun dataSave() = intent {

        val db = Firebase.firestore
        val userId = state.userDataList.find { it.id == "auth" }!!.value
        val userDataList = state.userDataList

        val userData = mapOf(
            "ban" to userDataList.find { it.id == "like"}!!.value3,
            "cash" to userDataList.find { it.id == "money"}!!.value2,
            "firstDate" to userDataList.find { it.id == "date"}!!.value3,
            "like" to userDataList.find { it.id == "like"}!!.value,
            "money" to userDataList.find { it.id == "money"}!!.value,
            "name" to userDataList.find { it.id == "name"}!!.value,
            "openItem" to userDataList.find { it.id == ""}!!.value,


            "name" to "Alice",
            "age" to 25,
            "isPremium" to true,
            "tags" to listOf("android", "compose", "kotlin"),
            "profile" to mapOf(
                "bio" to "Jetpack Compose 좋아함",
                "location" to "Seoul"
            ),
        )

        db.collection("users")
            .document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "데이터 저장 성공")
                // UI 상태 업데이트 등
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "저장 실패", e)
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

                reduce {
                    state.copy(
                        googleLoginState = true
                    )
                }

                postSideEffect(SettingSideEffect.Toast("로그인 성공"))
            }
        } catch (e: Exception) {
            Log.e("login", "뷰모델 로그인 실패", e)
            postSideEffect(SettingSideEffect.Toast("로그인 실패: ${e.localizedMessage}"))
        } finally {
            reduce { state.copy(isLoggingIn = false) }
        }
    }

}


@Immutable
data class SettingState(
    val userDataList: List<User> = emptyList(),
    val isLoggingIn:Boolean = false,
    val googleLoginState: Boolean = false
    )


sealed interface SettingSideEffect {
    class Toast(val message: String) : SettingSideEffect
    data object NavigateToLoginScreen : SettingSideEffect
}
