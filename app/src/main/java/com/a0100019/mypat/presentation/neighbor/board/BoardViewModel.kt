package com.a0100019.mypat.presentation.neighbor.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.world.WorldDao
import com.a0100019.mypat.presentation.neighbor.chat.ChatSideEffect
import com.google.firebase.Firebase
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
class BoardViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao
) : ViewModel(), ContainerHost<BoardState, BoardSideEffect> {

    override val container: Container<BoardState, BoardSideEffect> = container(
        initialState = BoardState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(BoardSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
        loadBoardMessages()
    }

    //room에서 데이터 가져옴
    private fun loadData() = intent {
        val userDataList = userDao.getAllUserData()
        val patDataList = patDao.getAllPatData()
        val itemDataList = itemDao.getAllItemDataWithShadow()
        val allUserDataList = allUserDao.getAllUserDataNoBan()
//        allUserDataList = allUserDataList.filter { it.totalDate != "1" && it.totalDate != "0" }

        val allAreaCount = areaDao.getAllAreaData().size.toString()

        reduce {
            state.copy(
                userDataList = userDataList,
                patDataList = patDataList,
                itemDataList = itemDataList,
                allUserDataList =  allUserDataList,
                allAreaCount = allAreaCount
            )
        }
    }

    fun onClose() = intent {
        reduce {
            state.copy(
                situation = "",
                text = "",
                boardAnonymous = "0",
                boardType = "free",

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

    fun loadBoardMessages() = intent {

        val myTag = userDao.getAllUserData()
            .find { it.id == "auth" }!!
            .value2

        val boardRef = Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")

        // 1️⃣ 전체 게시글 100개
        boardRef
            .orderBy(
                com.google.firebase.firestore.FieldPath.documentId(),
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
            .limit(100)
            .get()
            .addOnSuccessListener { snapshot ->

                val boardMessages = snapshot.documents.mapNotNull { doc ->
                    val timestamp = doc.id.toLongOrNull() ?: return@mapNotNull null
                    val data = doc.data ?: return@mapNotNull null

                    BoardMessage(
                        timestamp = timestamp,
                        message = data["message"] as String,
                        name = data["name"] as String,
                        tag = data["tag"] as String,
                        ban = data["ban"] as String,
                        uid = data["uid"] as String,
                        type = data["type"] as String,
                        anonymous = data["anonymous"] as String
                    )
                }.sortedBy { it.timestamp }

                // 2️⃣ 내 게시글 전부 (limit ❌)
                boardRef
                    .whereEqualTo("tag", myTag) // 👉 uid 추천
                    .orderBy(
                        com.google.firebase.firestore.FieldPath.documentId(),
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                    )
                    .get()
                    .addOnSuccessListener { mySnapshot ->

                        val myBoardMessages = mySnapshot.documents.mapNotNull { doc ->
                            val timestamp = doc.id.toLongOrNull() ?: return@mapNotNull null
                            val data = doc.data ?: return@mapNotNull null

                            BoardMessage(
                                timestamp = timestamp,
                                message = data["message"] as String,
                                name = data["name"] as String,
                                tag = data["tag"] as String,
                                ban = data["ban"] as String,
                                uid = data["uid"] as String,
                                type = data["type"] as String,
                                anonymous = data["anonymous"] as String
                            )
                        }.sortedBy { it.timestamp }

                        viewModelScope.launch {
                            intent {
                                reduce {
                                    state.copy(
                                        boardMessages = boardMessages,      // ✅ 전체 100개
                                        myBoardMessages = myBoardMessages   // ✅ 내 글 전부
                                    )
                                }
                            }
                        }
                    }
            }
            .addOnFailureListener { e ->
                Log.e("BoardViewModel", "보드 메시지 로드 실패: ${e.message}")
            }
    }


    fun onBoardMessageClick(boardTimestamp: String) = intent {

        userDao.update(id = "etc2", value3 = boardTimestamp)
        postSideEffect(BoardSideEffect.NavigateToBoardMessageScreen)

    }

    fun onBoardTypeChange(type: String) = intent {

        reduce {
            state.copy(
                boardType = type
            )
        }
    }

    fun onBoardAnonymousChange(anonymous: String) = intent {

        reduce {
            state.copy(
                boardAnonymous = anonymous
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

    fun onBoardSubmitClick() = intent {

        val currentMessage = state.text.trim()

        // ❌ 10자 이하 → 토스트
        if (currentMessage.length < 5) {
            postSideEffect(BoardSideEffect.Toast("5자 이상 입력해주세요."))
            return@intent
        }

        val userName = state.userDataList.find { it.id == "name" }!!.value
        val userId = state.userDataList.find { it.id == "auth" }!!.value
        val userTag = state.userDataList.find { it.id == "auth" }!!.value2
        val userBan = state.userDataList.find { it.id == "community" }!!.value3

        val timestamp = System.currentTimeMillis()

        val boardData = mapOf(
            "message" to currentMessage,
            "name" to userName,
            "tag" to userTag,
            "ban" to userBan,
            "uid" to userId,
            "type" to state.boardType,        // ex) worry / free
            "anonymous" to state.boardAnonymous // "0" / "1"
        )

        Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")
            .document(timestamp.toString()) // 🔑 문서명 = timestamp
            .set(boardData)
            .addOnSuccessListener {
                Log.d("BoardSubmit", "보드 글 작성 성공")

                viewModelScope.launch {
                    reduce {
                        state.copy(
                            situation = "boardSubmitConfirm"
                        )
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BoardSubmit", "보드 글 작성 실패: ${e.message}")
                viewModelScope.launch {
                    postSideEffect(BoardSideEffect.Toast("작성 실패"))
                }
            }

    }

}

@Immutable
data class BoardState(
    val userDataList: List<User> = emptyList(),
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val allUserDataList: List<AllUser> = emptyList(),
    val situation: String = "",
    val clickAllUserData: AllUser = AllUser(),
    val clickAllUserWorldDataList: List<String> = emptyList(),
    val allAreaCount: String = "",
    val boardMessages: List<BoardMessage> = emptyList(),
    val myBoardMessages: List<BoardMessage> = emptyList(),
    val text: String = "",
    val boardType: String = "free",
    val boardAnonymous: String = "0"

    )

@Immutable
data class BoardMessage(
    val timestamp: Long = 0L,
    val message: String = "",
    val name: String = "",
    val tag: String = "",
    val ban: String = "0",
    val uid: String = "",
    val type: String = "",
    val anonymous: String = "0"
)

//상태와 관련없는 것
sealed interface BoardSideEffect{
    class Toast(val message:String): BoardSideEffect
    data object NavigateToBoardMessageScreen: BoardSideEffect

}