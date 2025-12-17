package com.a0100019.mypat.presentation.privateChat

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.setting.SettingSideEffect
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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

                    // 🔥 내가 user1인지 user2인지 판별 후 기준 last 값 결정
                    val myLast = if (myTag == user1) last1 else last2

                    // 🔥 message 컬렉션의 날짜문서 읽기
                    Firebase.firestore
                        .collection("chatting")
                        .document("privateChat")
                        .collection("privateChat")
                        .document(roomId)
                        .collection("message")
                        .get()
                        .addOnSuccessListener { dateDocs ->

                            var messageCount = 0

                            dateDocs.documents.forEach { dateDoc ->

                                val data = dateDoc.data ?: emptyMap<String, Any>()

                                // 날짜문서 안의 timestamp map들을 순회하며 last 기준으로 필터링
                                data.forEach { (key, value) ->

                                    val timestamp = key.toLongOrNull() ?: return@forEach

                                    // 🔥 last1 또는 last2 보다 최신 것만 카운트
                                    if (timestamp > myLast) {
                                        messageCount++
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
                                messageCount = messageCount
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

        if(roomCount >= 10) {
            //매달, medal, 칭호20
            val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

            val myMedalList: MutableList<Int> =
                myMedal
                    .split("/")
                    .mapNotNull { it.toIntOrNull() }
                    .toMutableList()

            // 🔥 여기 숫자 두개랑 위에 // 바꾸면 됨
            if (!myMedalList.contains(20)) {
                myMedalList.add(20)

                // 다시 문자열로 합치기
                val updatedMedal = myMedalList.joinToString("/")

                // DB 업데이트
                userDao.update(
                    id = "etc",
                    value3 = updatedMedal
                )

                postSideEffect(PrivateRoomSideEffect.Toast("칭호를 획득했습니다!"))
            }

        }

    }

    fun onPrivateChatRoomClick(roomId: String) = intent {

        userDao.update(id = "etc2", value3 = roomId)
        postSideEffect(PrivateRoomSideEffect.NavigateToPrivateChatInScreen)

    }

}

@Immutable
data class PrivateRoomState(
    val userDataList: List<User> = emptyList(),
    val chatMessages: List<PrivateChatMessage> = emptyList(),
    val roomList: List<PrivateRoom> = emptyList(),

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
)


//상태와 관련없는 것
sealed interface PrivateRoomSideEffect{
    class Toast(val message:String): PrivateRoomSideEffect

    data object NavigateToPrivateChatInScreen: PrivateRoomSideEffect
    data object NavigateToMainScreen: PrivateRoomSideEffect


}