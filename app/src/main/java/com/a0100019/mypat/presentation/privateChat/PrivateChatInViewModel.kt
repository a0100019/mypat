package com.a0100019.mypat.presentation.privateChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.neighbor.chat.ChatMessage
import com.a0100019.mypat.presentation.daily.diary.DiarySideEffect
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class PrivateChatInViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<PrivateChatInState, PrivateChatInSideEffect> {

    override val container: Container<PrivateChatInState, PrivateChatInSideEffect> = container(
        initialState = PrivateChatInState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(PrivateChatInSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
        loadChatMessages()
    }

    //room에서 데이터 가져옴
    private fun loadData() = intent {

        val userDataList = userDao.getAllUserData()

        reduce {
            state.copy(
                userDataList = userDataList,
            )
        }

    }

    private fun loadChatMessages() = intent {

        val userDataList = userDao.getAllUserData()
        val myTag = userDataList.find { it.id == "auth" }!!.value2
        val roomId = userDataList.find { it.id == "etc2" }!!.value3

        val roomRef = Firebase.firestore
            .collection("chatting")
            .document("privateChat")
            .collection("privateChat")
            .document(roomId)

        // 🔥 방 정보 먼저 불러오기
        roomRef.get()
            .addOnSuccessListener { roomSnap ->

                val user1 = roomSnap.getString("user1") ?: ""
                val user2 = roomSnap.getString("user2") ?: ""
                val name1 = roomSnap.getString("name1") ?: ""
                val name2 = roomSnap.getString("name2") ?: ""

                // 🔥 상대 이름
                val yourName =
                    if (myTag == user1) name2 else name1

                // 🔥 내 last 필드 결정
                val lastField = when (myTag) {
                    user1 -> "last1"
                    user2 -> "last2"
                    else -> null
                }

                // 🔥 채팅방 진입 시 last 업데이트
                if (lastField != null) {
                    roomRef.update(lastField, System.currentTimeMillis())
                        .addOnFailureListener {
                            Log.e("PrivateChatIn", "last 업데이트 실패: ${it.message}")
                        }
                }

                // 🔥 상대 이름 state 반영
                viewModelScope.launch {
                    intent {
                        reduce { state.copy(yourName = yourName) }
                    }
                }

                // 🔥 메시지 스냅샷 리스너
                roomRef.collection("message")
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {
                            Log.e("PrivateChatIn", "메시지 스냅샷 에러: ${error.message}")
                            return@addSnapshotListener
                        }

                        if (snapshot == null || snapshot.isEmpty) {
                            Log.w("PrivateChatIn", "메시지 없음")
                            return@addSnapshotListener
                        }

                        val allMessages = mutableListOf<PrivateChatMessage>()

                        // 날짜 문서 반복
                        for (dateDoc in snapshot.documents) {
                            val data = dateDoc.data ?: continue

                            // timestamp 필드 반복
                            for ((timestampKey, value) in data) {

                                val timestamp = timestampKey.toLongOrNull() ?: continue
                                val map = value as? Map<*, *> ?: continue

                                val message = map["message"] as? String ?: continue
                                val name = map["name"] as? String ?: continue
                                val tag = map["tag"] as? String ?: continue

                                allMessages.add(
                                    PrivateChatMessage(
                                        timestamp = timestamp,
                                        message = message,
                                        name = name,
                                        tag = tag
                                    )
                                )
                            }
                        }

                        val sorted = allMessages.sortedBy { it.timestamp }

                        viewModelScope.launch {
                            intent {
                                reduce { state.copy(chatMessages = sorted) }
                            }
                        }
                    }
            }
    }

    fun onChatSubmitClick() = intent {

        val userDataList = userDao.getAllUserData()

        val myName = userDataList.find { it.id == "name" }?.value ?: "익명"
        val myTag = userDataList.find { it.id == "auth" }?.value2 ?: ""
        val roomId = userDataList.find { it.id == "etc2" }!!.value3

        val text = state.text.trim()
        if (text.isEmpty()) return@intent

        val dateId = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val now = System.currentTimeMillis()
        val timestampKey = now.toString()

        val messageData = mapOf(
            "message" to text,
            "name" to myName,
            "tag" to myTag
        )

        val baseRef = Firebase.firestore
            .collection("chatting")
            .document("privateChat")
            .collection("privateChat")
            .document(roomId)

        val messageRef = baseRef
            .collection("message")
            .document(dateId)

        // 🔥 user1 / user2 확인
        baseRef.get().addOnSuccessListener { roomDoc ->

            val user1 = roomDoc.getString("user1")
            val user2 = roomDoc.getString("user2")

            val nameField: String
            val lastField: String

            when (myTag) {
                user1 -> {
                    nameField = "name1"
                    lastField = "last1"
                }
                user2 -> {
                    nameField = "name2"
                    lastField = "last2"
                }
                else -> {
                    Log.e("PrivateChatIn", "내 userId가 user1/user2와 일치하지 않음")
                    return@addOnSuccessListener
                }
            }

            // 🔥 메시지 + 이름 + last 동시에 처리
            Firebase.firestore.runBatch { batch ->

                // 메시지 저장
                batch.set(
                    messageRef,
                    mapOf(timestampKey to messageData),
                    SetOptions.merge()
                )

                // 내 이름 업데이트
                batch.update(baseRef, nameField, myName)

                // 🔥 내 last 업데이트 (읽음 기준)
                batch.update(baseRef, lastField, now)

            }.addOnSuccessListener {
                Log.d("PrivateChatIn", "메시지 + 이름 + last 업데이트 완료")

                // 입력창 비우기
                intent { reduce { state.copy(text = "") } }

            }.addOnFailureListener {
                Log.e("PrivateChatIn", "저장 실패: ${it.message}")
            }

        }.addOnFailureListener {
            Log.e("PrivateChatIn", "roomId 문서 로드 실패: ${it.message}")
        }
    }


    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onTextChange(text: String) = blockingIntent {

        reduce {
            state.copy(text = text)
        }

    }


}

@Immutable
data class PrivateChatInState(
    val userDataList: List<User> = emptyList(),
    val chatMessages: List<PrivateChatMessage> = emptyList(),
    val text: String = "",
    val yourName: String = "",
    )

@Immutable
data class PrivateChatMessage(
    val timestamp: Long,
    val message: String,
    val name: String,
    val tag: String,
)

//상태와 관련없는 것
sealed interface PrivateChatInSideEffect{
    class Toast(val message:String): PrivateChatInSideEffect

    data object NavigateToPrivateRoomScreen: PrivateChatInSideEffect

}