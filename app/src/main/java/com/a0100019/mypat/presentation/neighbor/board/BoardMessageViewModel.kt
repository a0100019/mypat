package com.a0100019.mypat.presentation.neighbor.board

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
import com.a0100019.mypat.presentation.information.addMedalAction
import com.a0100019.mypat.presentation.information.getMedalActionCount
import com.a0100019.mypat.presentation.privateChat.PrivateRoom
import com.google.firebase.Firebase
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
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class BoardMessageViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao
) : ViewModel(), ContainerHost<BoardMessageState, BoardMessageSideEffect> {

    override val container: Container<BoardMessageState, BoardMessageSideEffect> = container(
        initialState = BoardMessageState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(BoardMessageSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
        loadBoardMessage()
    }

    //room에서 데이터 가져옴
    private fun loadData() = intent {

    }

    fun onClose() = intent {
        reduce {
            state.copy(

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

    private fun loadBoardMessage() = intent {

        val userDataList = userDao.getAllUserData()
        val boardTimestamp =
            userDataList.find { it.id == "etc2" }!!.value3  // 문서명(timestamp)

        val boardRef = Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")
            .document(boardTimestamp)

        boardRef.addSnapshotListener { snap, error ->

            if (error != null) {
                Log.e("BoardLoad", "게시글 구독 실패: ${error.message}")
                return@addSnapshotListener
            }

            if (snap == null || !snap.exists()) return@addSnapshotListener

            /* ---------------------------
             * 1️⃣ boardData (문서 필드 그대로)
             * timestamp = 문서명
             * --------------------------- */
            val boardData = BoardMessage(
                timestamp = boardTimestamp.toLong(),
                message = snap.getString("message") ?: "",
                name = snap.getString("name") ?: "",
                tag = snap.getString("tag") ?: "",
                ban = snap.getString("ban") ?: "",
                uid = snap.getString("uid") ?: "",
                type = snap.getString("type") ?: "",
                anonymous = snap.getString("anonymous") ?: ""
            )

            /* ---------------------------
             * 2️⃣ boardChat (answer 맵)
             * --------------------------- */
            val boardChatList = mutableListOf<BoardChatMessage>()

            val answerMap = snap.get("answer") as? Map<*, *> ?: emptyMap<Any, Any>()

            for ((timestampKey, value) in answerMap) {

                val timestamp = timestampKey.toString().toLongOrNull() ?: continue
                val map = value as? Map<*, *> ?: continue

                boardChatList.add(
                    BoardChatMessage(
                        timestamp = timestamp,
                        message = map["message"] as? String ?: "",
                        name = map["name"] as? String ?: "",
                        tag = map["tag"] as? String ?: "",
                        ban = map["ban"] as? String ?: "",
                        uid = map["uid"] as? String ?: "",
                        anonymous = map["anonymous"] as? String ?: ""
                    )
                )
            }

            val sortedChat = boardChatList.sortedBy { it.timestamp }

            viewModelScope.launch {
                intent {
                    reduce {
                        state.copy(
                            boardData = boardData,
                            boardChat = sortedChat
                        )
                    }
                }
            }
        }
    }


    fun onAnonymousChange(anonymous: String) = intent {

        reduce {
            state.copy(
                anonymous = anonymous
            )
        }
    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onTextChange(text: String) = blockingIntent {

        reduce {
            state.copy(text = text)
        }
    }

    fun onBoardChatSubmitClick() = intent {

        val currentText = state.text.trim()
        if (currentText.isEmpty()) return@intent

        val userDataList = userDao.getAllUserData()

        val userName = userDataList.find { it.id == "name" }!!.value
        val userId = userDataList.find { it.id == "auth" }!!.value
        val userTag = userDataList.find { it.id == "auth" }!!.value2
        val userBan = userDataList.find { it.id == "community" }!!.value3

        val boardTimestamp =
            userDataList.find { it.id == "etc2" }!!.value3  // 게시글 문서명

        val timestamp = System.currentTimeMillis().toString()

        // 🔑 timestamp 안에 들어갈 데이터
        val answerData = mapOf(
            "message" to currentText,
            "name" to userName,
            "tag" to userTag,
            "ban" to userBan,
            "uid" to userId,
            "anonymous" to state.anonymous
        )

        // 🔑 answer 맵 구조를 명확히 만듦
        val updateMap = mapOf(
            "answer" to mapOf(
                timestamp to answerData
            )
        )

        Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")
            .document(boardTimestamp)
            .set(updateMap, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("BoardChatSubmit", "댓글 작성 성공")
                viewModelScope.launch {

                    var medalData = userDao.getAllUserData().find { it.id == "name" }!!.value2
                    medalData = addMedalAction(medalData, actionId = 13)
                    userDao.update(
                        id = "name",
                        value2 = medalData
                    )

                    if(getMedalActionCount(medalData, actionId = 13) >= 10) {
                        //매달, medal, 칭호13
                        val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

                        val myMedalList: MutableList<Int> =
                            myMedal
                                .split("/")
                                .mapNotNull { it.toIntOrNull() }
                                .toMutableList()

                        // 🔥 여기 숫자 두개랑 위에 // 바꾸면 됨
                        if (!myMedalList.contains(13)) {
                            myMedalList.add(13)

                            // 다시 문자열로 합치기
                            val updatedMedal = myMedalList.joinToString("/")

                            // DB 업데이트
                            userDao.update(
                                id = "etc",
                                value3 = updatedMedal
                            )

                            postSideEffect(BoardMessageSideEffect.Toast("칭호를 획득했습니다!"))
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BoardChatSubmit", "댓글 작성 실패: ${e.message}")
            }

        // 입력 초기화
        reduce {
            state.copy(text = "")
        }
    }


}

@Immutable
data class BoardMessageState(
    val userDataList: List<User> = emptyList(),
    val boardChat: List<BoardChatMessage> = emptyList(),
    val boardData: BoardMessage = BoardMessage(),
    val text: String = "",
    val anonymous: String = "0",
    val situation: String = "",

    )

@Immutable
data class BoardChatMessage(
    val timestamp: Long = 0L,
    val message: String = "0",
    val name: String = "0",
    val tag: String = "0",
    val ban: String = "0",
    val uid: String = "0",
    val anonymous: String = "0"
)


//상태와 관련없는 것
sealed interface BoardMessageSideEffect{
    class Toast(val message:String): BoardMessageSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}