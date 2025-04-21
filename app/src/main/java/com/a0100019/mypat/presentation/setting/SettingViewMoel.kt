package com.a0100019.mypat.presentation.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.pet.PatDao
import com.a0100019.mypat.data.room.sudoku.SudokuDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.data.room.world.WorldDao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@Suppress("IMPLICIT_CAST_TO_ANY")
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
        val itemDataList = itemDao.getAllItemData()
        val patDataList = patDao.getAllPatData()
        val worldDataList = worldDao.getAllWorldData()

        reduce {
            state.copy(
                userDataList = userDataList,
                googleLoginState = googleLoginState,
                itemDataList = itemDataList,
                patDataList = patDataList,
                worldDataList = worldDataList
            )
        }
    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                settingSituation = ""
            )
        }
    }

    fun onTermsClick() = intent {
        try {
            val uri = FirebaseStorage.getInstance()
                .reference.child("sample.png")
                .downloadUrl.await()

            reduce {
                state.copy(imageUrl = uri.toString())
            }
        } catch (e: Exception) {
            // 실패 처리 가능
        }
    }

    fun onSituationChange(situation: String) = intent {
        reduce {
            state.copy(
                settingSituation = situation
            )
        }
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
        val itemDataList = state.itemDataList
        val patDataList = state.patDataList
        val worldDataList = state.worldDataList

        val userData = mapOf(
            "cash" to userDataList.find { it.id == "money"}!!.value2,
            "community" to mapOf(
                "ban" to userDataList.find { it.id == "community"}!!.value3,
                "like" to userDataList.find { it.id == "community"}!!.value,
                "warning" to userDataList.find {it.id == "community"}!!.value2
            ),
            "firstDate" to userDataList.find { it.id == "date"}!!.value3,
            "item" to mapOf(
                "openItem" to itemDataList.count { it.date != "0"},
                "openItemSpace" to userDataList.find { it.id == "item"}!!.value2,
                "useItem" to userDataList.find { it.id == "item"}!!.value3
            ),
            "map" to worldDataList.find { it.id == 1}!!.value,
            "money" to userDataList.find { it.id == "money"}!!.value,
            "name" to userDataList.find { it.id == "name"}!!.value,
            "pat" to mapOf(
                "openPat" to patDataList.count { it.date != "0"},
                "openPatSpace" to userDataList.find { it.id == "pat"}!!.value2,
                "usePat" to userDataList.find { it.id == "pat"}!!.value3
            ),
            "totalDate" to userDataList.find { it.id == "date"}!!.value2,
        )

        //월드 데이터
        val worldMap = worldDataList.drop(1)
            .mapIndexed { index, data ->

                if(data.type == "pat") {
                    val patData = patDataList.find { it.id == data.value.toInt() }
                    // index는 0부터 시작하니까 +1 해서 문자열로 만듦
                    index.toString() to mapOf(
                        "id" to data.value,
                        "size" to patData!!.sizeFloat,
                        "type" to data.type,
                        "x" to patData.x,
                        "y" to patData.y
                    )
                } else {
                    val itemData = itemDataList.find { it.id == data.value.toInt() }
                    // index는 0부터 시작하니까 +1 해서 문자열로 만듦
                    index.toString() to mapOf(
                        "id" to data.value,
                        "size" to itemData!!.sizeFloat,
                        "type" to data.type,
                        "x" to itemData.x,
                        "y" to itemData.y
                    )
                }

            }
            .toMap()

        val result = mapOf("world" to worldMap)


        db.collection("users")
            .document(userId)
            .set(result)
            .addOnSuccessListener {
                Log.d("Firestore", "world 저장 성공")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "world 저장 실패", e)
            }

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
            loadData()
        }
    }

    fun onAccountDeleteClick() = intent {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        // 1. Firestore 데이터 먼저 삭제
        db.collection("users").document(state.userDataList.find {it.id == "auth"}!!.value).delete()
            .addOnSuccessListener {
                Log.d("Firestore", "유저 문서 삭제됨")

                // 2. Authentication 계정 삭제
                auth.currentUser?.delete()
                    ?.addOnSuccessListener {
                        viewModelScope.launch {
                            userDao.update(id = "auth", value = "0")
                            postSideEffect(SettingSideEffect.Toast("계정이 삭제되었습니다."))
                            postSideEffect(SettingSideEffect.NavigateToLoginScreen)
                        }
                        Log.d("Auth", "계정 삭제 완료")
                    }
                    ?.addOnFailureListener {
                        Log.e("Auth", "계정 삭제 실패", it)
                    }
            }
            .addOnFailureListener {
                Log.e("Firestore", "문서 삭제 실패", it)
            }
    }

}


@Immutable
data class SettingState(
    val userDataList: List<User> = emptyList(),
    val isLoggingIn:Boolean = false,
    val googleLoginState: Boolean = false,
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val worldDataList: List<World> = emptyList(),
    val settingSituation: String = "",
    val imageUrl: String = ""
    )


sealed interface SettingSideEffect {
    class Toast(val message: String) : SettingSideEffect
    data object NavigateToLoginScreen : SettingSideEffect
}
