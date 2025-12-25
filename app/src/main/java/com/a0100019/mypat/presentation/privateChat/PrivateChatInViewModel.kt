package com.a0100019.mypat.presentation.privateChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
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

        // 🔥 채팅방 정보 구독
        roomRef.addSnapshotListener { roomSnap, error ->

            if (error != null || roomSnap == null || !roomSnap.exists()) {
                Log.e("PrivateChatIn", "채팅방 구독 실패: ${error?.message}")
                return@addSnapshotListener
            }

            val privateChatData = PrivateChatData(
                roomId = roomId,
                user1 = roomSnap.getString("user1") ?: "",
                user2 = roomSnap.getString("user2") ?: "",
                name1 = roomSnap.getString("name1") ?: "",
                name2 = roomSnap.getString("name2") ?: "",
                lastTimestamp = roomSnap.getLong("lastTimestamp") ?: 0L,
                lastMessage = roomSnap.getString("lastMessage") ?: "",
                messageCount = (roomSnap.getLong("messageCount") ?: 0L).toInt(),
                attacker = roomSnap.getString("attacker") ?: "",
                highScore = (roomSnap.getLong("highScore") ?: 0L).toInt(),
                lastGame = roomSnap.getString("lastGame") ?: "2001-01-01",
                todayScore1 = (roomSnap.getLong("todayScore1") ?: 0L).toInt(),
                todayScore2 = (roomSnap.getLong("todayScore2") ?: 0L).toInt(),
                totalScore = (roomSnap.getLong("totalScore") ?: 0L).toInt()
            )

            val yourName =
                if (myTag == privateChatData.user1)
                    privateChatData.name2
                else
                    privateChatData.name1

            val yourTag =
                if (myTag == privateChatData.user1)
                    privateChatData.user2
                else
                    privateChatData.user1

            viewModelScope.launch {
                intent {
                    reduce {
                        state.copy(
                            privateChatData = privateChatData,
                            yourName = yourName,
                            yourTag = yourTag
                        )
                    }
                }
            }

            // 🔹 메시지 100개 이상 시 칭호 지급 (중복 방지용 체크 필요하면 flag 추가 가능)
            viewModelScope.launch {
                if (privateChatData.messageCount >= 100) {

                    val myMedal = userDao.getAllUserData()
                        .find { it.id == "etc" }!!.value3

                    val myMedalList = myMedal
                        .split("/")
                        .mapNotNull { it.toIntOrNull() }
                        .toMutableList()

                    //매달, medal, 칭호21
                    if (!myMedalList.contains(21)) {
                        myMedalList.add(21)

                        userDao.update(
                            id = "etc",
                            value3 = myMedalList.joinToString("/")
                        )

                        postSideEffect(
                            PrivateChatInSideEffect.Toast("칭호를 획득했습니다!")
                        )
                    }
                }

                if((roomSnap.getLong("highScore") ?: 0L).toInt() >= 100) {//매달, medal, 칭호24
                    val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

                    val myMedalList: MutableList<Int> =
                        myMedal
                            .split("/")
                            .mapNotNull { it.toIntOrNull() }
                            .toMutableList()

                    // 🔥 여기 숫자 두개 바꾸면 됨
                    if (!myMedalList.contains(24)) {
                        myMedalList.add(24)

                        // 다시 문자열로 합치기
                        val updatedMedal = myMedalList.joinToString("/")

                        // DB 업데이트
                        userDao.update(
                            id = "etc",
                            value3 = updatedMedal
                        )

                        postSideEffect(PrivateChatInSideEffect.Toast("칭호를 획득했습니다!"))
                    }
                }

                if((roomSnap.getLong("totalScore") ?: 0L).toInt() >= 1000) {
                    //매달, medal, 칭호25
                    val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

                    val myMedalList: MutableList<Int> =
                        myMedal
                            .split("/")
                            .mapNotNull { it.toIntOrNull() }
                            .toMutableList()

                    // 🔥 여기 숫자 두개 바꾸면 됨
                    if (!myMedalList.contains(25)) {
                        myMedalList.add(25)

                        // 다시 문자열로 합치기
                        val updatedMedal = myMedalList.joinToString("/")

                        // DB 업데이트
                        userDao.update(
                            id = "etc",
                            value3 = updatedMedal
                        )

                        postSideEffect(PrivateChatInSideEffect.Toast("칭호를 획득했습니다!"))
                    }
                }

            }
        }

        // 🔥 채팅방 진입 last 업데이트 (1회)
        val lastField = when (myTag) {
            userDataList.find { it.id == "auth" }!!.value2 -> "last1"
            else -> "last2"
        }

        roomRef.update(lastField, System.currentTimeMillis())

        // 🔥 메시지 구독
        roomRef.collection("message")
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    Log.e("PrivateChatIn", "메시지 스냅샷 에러: ${error?.message}")
                    return@addSnapshotListener
                }

                val allMessages = mutableListOf<PrivateChatMessage>()

                for (dateDoc in snapshot.documents) {
                    val data = dateDoc.data ?: continue

                    for ((timestampKey, value) in data) {
                        val timestamp = timestampKey.toLongOrNull() ?: continue
                        val map = value as? Map<*, *> ?: continue

                        allMessages.add(
                            PrivateChatMessage(
                                timestamp = timestamp,
                                message = map["message"] as? String ?: "",
                                name = map["name"] as? String ?: "",
                                tag = map["tag"] as? String ?: ""
                            )
                        )
                    }
                }

                val sortedMessages = allMessages.sortedBy { it.timestamp }

                viewModelScope.launch {
                    intent {
                        reduce {
                            state.copy(chatMessages = sortedMessages)
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

                batch.update(baseRef, "lastMessage", text)

                // 🔥 메시지 카운트 +1
                batch.update(baseRef, "messageCount", FieldValue.increment(1))

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

    fun onNeighborInformationClick() = intent {

        userDao.update(id = "etc2", value3 = state.yourTag)
        postSideEffect(PrivateChatInSideEffect.NavigateToNeighborInformationScreen)

    }

    fun onSituationChange(situation: String) = intent {

        reduce {
            state.copy(
                situation = situation
            )
        }
    }

    fun onClose() = intent {
        reduce {
            state.copy(
                situation = "",
                text = ""
            )
        }
    }

    fun onPrivateRoomDelete() = intent {

        val userDataList = userDao.getAllUserData()
        val roomId = userDataList.find { it.id == "etc2" }!!.value3

        val roomRef = Firebase.firestore
            .collection("chatting")
            .document("privateChat")
            .collection("privateChat")
            .document(roomId)

        // 1️⃣ message 하위 컬렉션 먼저 삭제
        roomRef.collection("message")
            .get()
            .addOnSuccessListener { snapshot ->

                val batch = Firebase.firestore.batch()

                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }

                // 2️⃣ batch 실행
                batch.commit()
                    .addOnSuccessListener {

                        // 3️⃣ room 문서 삭제
                        roomRef.delete()
                            .addOnSuccessListener {
                                Log.d("PrivateRoomDelete", "개인 채팅방 삭제 성공")
                                intent { reduce { state.copy(situation = "deleteCheck") } }
                            }
                            .addOnFailureListener { e ->
                                Log.e("PrivateRoomDelete", "방 문서 삭제 실패: ${e.message}")
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("PrivateRoomDelete", "메시지 삭제 실패: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PrivateRoomDelete", "message 컬렉션 로드 실패: ${e.message}")
            }
    }


}

@Immutable
data class PrivateChatInState(
    val userDataList: List<User> = emptyList(),
    val chatMessages: List<PrivateChatMessage> = emptyList(),
    val text: String = "",
    val yourName: String = "",
    val yourTag: String = "",
    val situation: String = "",
    val privateChatData: PrivateChatData = PrivateChatData()
    )

@Immutable
data class PrivateChatMessage(
    val timestamp: Long = 0L,
    val message: String = "",
    val name: String = "",
    val tag: String = "",
)

@Immutable
data class PrivateChatData(
    val roomId: String = "",
    val user1: String = "",
    val user2: String = "",
    val name1: String = "",
    val name2: String = "",
    val lastTimestamp: Long = 0L,
    val lastMessage: String = "",
    val messageCount: Int = 0,
    val attacker: String = "",
    val highScore: Int = 0,
    val lastGame: String = "2001-01-01",
    val todayScore1: Int = 0,
    val todayScore2: Int = 0,
    val totalScore: Int = 0,
    val todayScoreSum: Int = 0,
)

//상태와 관련없는 것
sealed interface PrivateChatInSideEffect{
    class Toast(val message:String): PrivateChatInSideEffect

    data object NavigateToPrivateRoomScreen: PrivateChatInSideEffect
    data object NavigateToNeighborInformationScreen: PrivateChatInSideEffect

}