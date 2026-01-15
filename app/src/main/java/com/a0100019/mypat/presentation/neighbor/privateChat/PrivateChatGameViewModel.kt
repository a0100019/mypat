package com.a0100019.mypat.presentation.neighbor.privateChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.world.WorldDao
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class PrivateChatGameViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao
) : ViewModel(), ContainerHost<PrivateChatGameState, PrivateChatGameSideEffect> {

    override val container: Container<PrivateChatGameState, PrivateChatGameSideEffect> = container(
        initialState = PrivateChatGameState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(PrivateChatGameSideEffect.Toast(message = throwable.message.orEmpty()))
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

        reduce {
            state.copy(
                userDataList = userDataList
            )
        }
    }

    fun onClose() = intent {
        reduce {
            state.copy(
                situation = ""
            )
        }
    }

    fun onSituationChange(situation: String) = intent {
        reduce {
            state.copy(
                situation = situation
            )
        }
    }

    private var gameJob: Job? = null

    fun onGameStartClick() = intent {

        val targetStart = (0..900).random()
        reduce {
            state.copy(
                situation = "진행중",
                currentValue = 0,
                targetStart = targetStart,
                targetEnd = targetStart + 100
            )
        }
        startPowerLoop()
    }

    private fun startPowerLoop() = intent {
        gameJob?.cancel()

        gameJob = viewModelScope.launch {
            var value = 0
            var direction = 1 // 1 = 증가, -1 = 감소

            while (isActive) {
                value += direction * (20 + state.score)// 속도 조절 (숫자 클수록 빠름)

                if (value >= 1000) {
                    value = 1000
                    direction = -1
                } else if (value <= 0) {
                    value = 0
                    direction = 1
                }

                intent {
                    reduce {
                        state.copy(currentValue = value)
                    }
                }

                delay(30L) // 프레임 속도 (작을수록 부드러움)
            }
        }
    }

    fun onAttackClick() = intent {
        gameJob?.cancel()
        gameJob = null

        val isSuccess =
            state.currentValue in state.targetStart..state.targetEnd

        reduce {
            state.copy(
                situation = if (isSuccess) "성공" else "종료",
                score = if (isSuccess) state.score + 1 else state.score
            )
        }

        if(!isSuccess) {
            onGameOver()
        }

    }

    private fun onGameOver() = intent {

        val userDataList = userDao.getAllUserData()

        val roomId =
            userDataList.find { it.id == "etc2" }!!.value3

        val myTag =
            userDataList.find { it.id == "auth" }!!.value2

        val todayMessageDoc =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        val roomRef = Firebase.firestore
            .collection("chatting")
            .document("privateChat")
            .collection("privateChat")
            .document(roomId)

        roomRef.get()
            .addOnSuccessListener { snap ->

                if (!snap.exists()) return@addOnSuccessListener

                val user1 = snap.getString("user1") ?: return@addOnSuccessListener
                val user2 = snap.getString("user2") ?: return@addOnSuccessListener

                val totalScore = (snap.getLong("totalScore") ?: 0L).toInt()
                val newScore = state.score
                val nowTimestamp = System.currentTimeMillis()

                val updates = mutableMapOf<String, Any>()

                // ===============================
                // 🔥 내가 user1 인 경우
                // ===============================
                if (myTag == user1) {
                    updates["attacker"] = user2
                    updates["totalScore"] = totalScore + newScore
                    updates["last1"] = nowTimestamp
                }

                // ===============================
                // 🔥 내가 user2 인 경우
                // ===============================
                if (myTag == user2) {
                    updates["attacker"] = user1
                    updates["totalScore"] = totalScore + newScore
                    updates["last2"] = nowTimestamp
                }

                // ===============================
                // 🔥 채팅방 데이터 업데이트
                // ===============================
                if (updates.isNotEmpty()) {
                    roomRef.update(updates)
                }

                // ===============================
                // 🔥 system 메시지 추가
                // ===============================
                val timestampKey = nowTimestamp.toString()

                val nextTurnTag =
                    if (myTag == user1) user2 else user1

                val systemMessage = mapOf(
                    "message" to "#${myTag} 님 출격! ⚔️ $newScore 점 획득!\n다음은 #${nextTurnTag} 님 차례입니다!",
                    "tag" to "0",
                    "name" to "system"
                )

                roomRef
                    .collection("message")
                    .document(todayMessageDoc)
                    .set(
                        mapOf(timestampKey to systemMessage),
                        SetOptions.merge()
                    )
            }

        // 🎯 첫 공격 → 칭호24
        val myMedal = userDao.getAllUserData()
            .find { it.id == "etc" }!!.value3

        val myMedalList = myMedal
            .split("/")
            .mapNotNull { it.toIntOrNull() }
            .toMutableList()

        if (!myMedalList.contains(24)) {
            myMedalList.add(24)

            userDao.update(
                id = "etc",
                value3 = myMedalList.joinToString("/")
            )

            postSideEffect(
                PrivateChatGameSideEffect.Toast("칭호를 획득했습니다!")
            )
        }

    }


    private fun stopPowerLoop() = intent {
        gameJob?.cancel()
        gameJob = null

    }

}

@Immutable
data class PrivateChatGameState(
    val userDataList: List<User> = emptyList(),
    val situation: String = "준비",
    val currentValue: Int = 500,
    val targetStart: Int = 450,
    val targetEnd: Int = 550,
    val score: Int = 0,

)

//상태와 관련없는 것
sealed interface PrivateChatGameSideEffect{
    class Toast(val message:String): PrivateChatGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}