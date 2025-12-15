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
import com.a0100019.mypat.presentation.privateChat.PrivateRoomSideEffect
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
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
                situation = ""
            )
        }
    }

    private fun loadBoardMessages() = intent {

        Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")
            // 🔑 문서명(timestamp) 기준 최신순
            .orderBy(
                com.google.firebase.firestore.FieldPath.documentId(),
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
            .limit(100)
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {
                    Log.w("BoardViewModel", "board 컬렉션에 문서 없음")
                    return@addOnSuccessListener
                }

                val boardMessages = snapshot.documents.mapNotNull { doc ->

                    // 🔑 문서명 = timestamp
                    val timestamp = doc.id.toLongOrNull() ?: return@mapNotNull null
                    val data = doc.data ?: return@mapNotNull null

                    val message = data["message"] as? String ?: return@mapNotNull null
                    val name = data["name"] as? String ?: return@mapNotNull null
                    val tag = data["tag"] as? String ?: return@mapNotNull null
                    val ban = data["ban"] as? String ?: return@mapNotNull null
                    val uid = data["uid"] as? String ?: return@mapNotNull null
                    val type = data["type"] as? String ?: return@mapNotNull null
                    val anonymous = data["anonymous"] as? String ?: return@mapNotNull null

                    BoardMessage(
                        timestamp = timestamp,
                        message = message,
                        name = name,
                        tag = tag,
                        ban = ban,
                        uid = uid,
                        type = type,
                        anonymous = anonymous
                    )
                }
                    // ⏱ UI용으로 다시 시간 오름차순
                    .sortedBy { it.timestamp }

                viewModelScope.launch {
                    intent {
                        reduce {
                            state.copy(boardMessages = boardMessages)
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

}

@Immutable
data class BoardState(
    val userDataList: List<User> = emptyList(),
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val allUserDataList: List<AllUser> = emptyList(),
    val situation: String = "world",
    val clickAllUserData: AllUser = AllUser(),
    val clickAllUserWorldDataList: List<String> = emptyList(),
    val newChat: String = "",
    val allAreaCount: String = "",
    val boardMessages: List<BoardMessage> = emptyList(),

    )

@Immutable
data class BoardMessage(
    val timestamp: Long,
    val message: String,
    val name: String,
    val tag: String,
    val ban: String,
    val uid: String,
    val type: String,
    val anonymous: String
)

//상태와 관련없는 것
sealed interface BoardSideEffect{
    class Toast(val message:String): BoardSideEffect
    data object NavigateToBoardMessageScreen: BoardSideEffect

}