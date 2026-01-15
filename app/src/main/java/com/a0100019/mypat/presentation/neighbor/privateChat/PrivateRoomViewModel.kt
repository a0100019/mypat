package com.a0100019.mypat.presentation.neighbor.privateChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.neighbor.chat.ChatSideEffect
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
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
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class PrivateRoomViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<PrivateRoomState, PrivateRoomSideEffect> {

    override val container: Container<PrivateRoomState, PrivateRoomSideEffect> = container(
        initialState = PrivateRoomState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(PrivateRoomSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
        loadMyRooms()
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

    fun loadMyRooms() = intent {

        val userDataList = userDao.getAllUserData()
        val myTag = userDataList.find { it.id == "auth" }!!.value2

        var roomCount = 0

        val roomRef = Firebase.firestore
            .collection("chatting")
            .document("privateChat")
            .collection("privateChat")

        roomRef
            .whereArrayContains("participants", myTag)
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {
                    intent { reduce { state.copy(roomList = emptyList()) } }
                    return@addOnSuccessListener
                }

                val totalRooms = snapshot.size()
                roomCount = totalRooms
                var completed = 0

                val roomsList = mutableListOf<PrivateRoom>()

                snapshot.documents.forEach { doc ->

                    val roomId = doc.id

                    val user1 = doc.getString("user1") ?: ""
                    val user2 = doc.getString("user2") ?: ""
                    val name1 = doc.getString("name1") ?: ""
                    val name2 = doc.getString("name2") ?: ""

                    val last1 = doc.getLong("last1") ?: 0L
                    val last2 = doc.getLong("last2") ?: 0L

                    val lastMessage = doc.getString("lastMessage") ?: ""

                    val highScore = (doc.getLong("highScore") ?: 0L).toInt()
                    val totalScore = (doc.getLong("totalScore") ?: 0L).toInt()
                    val attacker = doc.getString("attacker") ?: ""

                    // 🔥 내가 user1인지 user2인지 판별
                    val myLast = if (myTag == user1) last1 else last2

                    // 🔥 message 컬렉션에서 안 읽은 메시지 개수 계산
                    Firebase.firestore
                        .collection("chatting")
                        .document("privateChat")
                        .collection("privateChat")
                        .document(roomId)
                        .collection("message")
                        .get()
                        .addOnSuccessListener { dateDocs ->

                            var unreadCount = 0

                            dateDocs.documents.forEach { dateDoc ->
                                val data = dateDoc.data ?: emptyMap<String, Any>()

                                data.forEach { (key, _) ->
                                    val timestamp = key.toLongOrNull() ?: return@forEach
                                    if (timestamp > myLast) {
                                        unreadCount++
                                    }
                                }
                            }

                            val roomItem = PrivateRoom(
                                roomId = roomId,
                                user1 = user1,
                                user2 = user2,
                                name1 = name1,
                                name2 = name2,
                                lastTimestamp = maxOf(last1, last2),
                                lastMessage = lastMessage,
                                messageCount = unreadCount,
                                highScore = highScore,
                                totalScore = totalScore,
                                attacker = attacker
                            )

                            roomsList.add(roomItem)
                            completed++

                            if (completed == totalRooms) {
                                val sorted = roomsList.sortedByDescending { it.messageCount }
                                intent {
                                    reduce { state.copy(roomList = sorted) }
                                }
                            }
                        }
                }
            }

        // 🔥 방 10개 이상 → 칭호 지급
        if (roomCount >= 10) {

            val myMedal = userDao.getAllUserData()
                .find { it.id == "etc" }!!.value3

            val myMedalList =
                myMedal.split("/")
                    .mapNotNull { it.toIntOrNull() }
                    .toMutableList()

            if (!myMedalList.contains(20)) {
                myMedalList.add(20)

                userDao.update(
                    id = "etc",
                    value3 = myMedalList.joinToString("/")
                )

                postSideEffect(
                    PrivateRoomSideEffect.Toast("칭호를 획득했습니다!")
                )
            }
        }
    }

    fun onPrivateChatRoomClick(roomId: String) = intent {

        userDao.update(id = "etc2", value3 = roomId)
        postSideEffect(PrivateRoomSideEffect.NavigateToPrivateChatInScreen)

    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onYourTagChange(text: String) = blockingIntent {

        reduce {
            state.copy(yourTag = text)
        }

    }

    fun onSituationChange(text: String) = intent {
        reduce {
            state.copy(
                situation = text
            )
        }
    }

    fun onClose() = intent {
        reduce {
            state.copy(
                situation = "",
                yourTag = "",
            )
        }
    }

    fun onRankClick() = intent {

        val roomRef = Firebase.firestore
            .collection("chatting")
            .document("privateChat")
            .collection("privateChat")

        roomRef
            .orderBy("highScore", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {
                    intent {
                        reduce {
                            state.copy(roomListRank = emptyList())
                        }
                    }
                    return@addOnSuccessListener
                }

                val rankList = snapshot.documents.map { doc ->

                    PrivateRoom(
                        roomId = doc.id,
                        user1 = doc.getString("user1") ?: "",
                        user2 = doc.getString("user2") ?: "",
                        name1 = doc.getString("name1") ?: "",
                        name2 = doc.getString("name2") ?: "",
                        lastTimestamp = maxOf(
                            doc.getLong("last1") ?: 0L,
                            doc.getLong("last2") ?: 0L
                        ),
                        lastMessage = doc.getString("lastMessage") ?: "",
                        messageCount = (doc.getLong("messageCount") ?: 0L).toInt(),
                        highScore = (doc.getLong("highScore") ?: 0L).toInt(),
                        totalScore = (doc.getLong("totalScore") ?: 0L).toInt(),
                        attacker = doc.getString("attacker") ?: ""
                    )
                }

                intent {
                    reduce {
                        state.copy(roomListRank = rankList)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Rank", "랭킹 불러오기 실패: ${it.message}")
            }

        roomRef
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {
                    intent {
                        reduce {
                            state.copy(roomListRank = emptyList())
                        }
                    }
                    return@addOnSuccessListener
                }

                val rankList = snapshot.documents.map { doc ->

                    PrivateRoom(
                        roomId = doc.id,
                        user1 = doc.getString("user1") ?: "",
                        user2 = doc.getString("user2") ?: "",
                        name1 = doc.getString("name1") ?: "",
                        name2 = doc.getString("name2") ?: "",
                        lastTimestamp = maxOf(
                            doc.getLong("last1") ?: 0L,
                            doc.getLong("last2") ?: 0L
                        ),
                        lastMessage = doc.getString("lastMessage") ?: "",
                        messageCount = (doc.getLong("messageCount") ?: 0L).toInt(),
                        highScore = (doc.getLong("highScore") ?: 0L).toInt(),
                        totalScore = (doc.getLong("totalScore") ?: 0L).toInt(),
                        attacker = doc.getString("attacker") ?: ""
                    )
                }

                intent {
                    reduce {
                        state.copy(roomListTotalRank = rankList)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Rank", "랭킹 불러오기 실패: ${it.message}")
            }
    }

    fun onNeighborInformationClick(neighborTag: String) = intent {

        userDao.update(id = "etc2", value3 = neighborTag)
        postSideEffect(PrivateRoomSideEffect.NavigateToNeighborInformationScreen)
        reduce {
            state.copy(
                yourTag = ""
            )
        }

    }

}

@Immutable
data class PrivateRoomState(
    val userDataList: List<User> = emptyList(),
    val roomList: List<PrivateRoom> = emptyList(),
    val roomListRank: List<PrivateRoom> = emptyList(),
    val roomListTotalRank: List<PrivateRoom> = emptyList(),
    val yourTag: String = "",
    val situation: String = "",
    )

@Immutable
data class PrivateRoom(
    val roomId: String = "",
    val user1: String = "",
    val user2: String = "",
    val name1: String = "",
    val name2: String = "",
    val lastTimestamp: Long = 0L,
    val lastMessage: String = "",
    val messageCount: Int = 0,
    val highScore: Int = 0,
    val totalScore: Int = 0,
    val attacker: String = "",
)

//상태와 관련없는 것
sealed interface PrivateRoomSideEffect{
    class Toast(val message:String): PrivateRoomSideEffect

    data object NavigateToPrivateChatInScreen: PrivateRoomSideEffect
    data object NavigateToMainScreen: PrivateRoomSideEffect
    data object NavigateToNeighborInformationScreen: PrivateRoomSideEffect

}