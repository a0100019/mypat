package com.a0100019.mypat.presentation.neighbor.board

import android.app.Activity
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
import com.a0100019.mypat.presentation.daily.english.EnglishSideEffect
import com.a0100019.mypat.presentation.main.management.RewardAdManager
import com.a0100019.mypat.presentation.main.management.addMedalAction
import com.a0100019.mypat.presentation.main.management.getMedalActionCount
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
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
class BoardViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao,
    private val rewardAdManager: RewardAdManager
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
        val removeAd = userDataList.find { it.id == "name" }!!.value3

        val allAreaCount = areaDao.getAllAreaData().size.toString()

        reduce {
            state.copy(
                userDataList = userDataList,
                patDataList = patDataList,
                itemDataList = itemDataList,
                allUserDataList =  allUserDataList,
                allAreaCount = allAreaCount,
                removeAd = removeAd
            )
        }
    }

    fun onClose() = intent {
        reduce {
            state.copy(
                situation = "",
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
            .find { it.id == "auth" }
            ?.value2
            ?: return@intent

        val boardRef = Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")

        // 1️⃣ 전체 게시글 100개 (ban == "1" 제외)
        boardRef
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .addOnSuccessListener { snapshot ->

                val boardMessages = snapshot.documents.mapNotNull { doc ->
                    val timestamp = doc.id.toLongOrNull() ?: return@mapNotNull null
                    val data = doc.data ?: return@mapNotNull null

                    val ban = data["ban"] as? String ?: "0"
                    if (ban == "1") return@mapNotNull null  // 🔥 차단된 글 제외

                    BoardMessage(
                        timestamp = timestamp,
                        message = data["message"] as? String ?: "",
                        name = data["name"] as? String ?: "알수없음",
                        tag = data["tag"] as? String ?: "",
                        ban = ban,
                        uid = data["uid"] as? String ?: "",
                        type = data["type"] as? String ?: "free",
                        anonymous = data["anonymous"] as? String ?: "0",
                        answerCount = (data["answer"] as? Map<*, *>)?.size ?: 0
                    )
                }.sortedBy { it.timestamp }

                // 2️⃣ 내 게시글 전부 (ban == "1" 제외)
                boardRef
                    .whereEqualTo("tag", myTag)
                    .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { mySnapshot ->

                        val myBoardMessages = mySnapshot.documents.mapNotNull { doc ->
                            val timestamp = doc.id.toLongOrNull() ?: return@mapNotNull null
                            val data = doc.data ?: return@mapNotNull null

                            val ban = data["ban"] as? String ?: "0"
                            if (ban == "1") return@mapNotNull null  // 🔥 차단된 글 제외

                            BoardMessage(
                                timestamp = timestamp,
                                message = data["message"] as? String ?: "",
                                name = data["name"] as? String ?: "알수없음",
                                tag = data["tag"] as? String ?: "",
                                ban = ban,
                                uid = data["uid"] as? String ?: "",
                                type = data["type"] as? String ?: "free",
                                anonymous = data["anonymous"] as? String ?: "0"
                            )
                        }.sortedBy { it.timestamp }

                        intent {
                            reduce {
                                state.copy(
                                    boardMessages = boardMessages,
                                    myBoardMessages = myBoardMessages
                                )
                            }
                        }
                    }
            }
            .addOnFailureListener { e ->
                Log.e("BoardViewModel", "보드 메시지 로드 실패", e)
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

                    var medalData = userDao.getAllUserData().find { it.id == "name" }!!.value2
                    medalData = addMedalAction(medalData, actionId = 12)
                    userDao.update(
                        id = "name",
                        value2 = medalData
                    )

                    if(getMedalActionCount(medalData, actionId = 12) >= 3) {
                        //매달, medal, 칭호12
                        val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

                        val myMedalList: MutableList<Int> =
                            myMedal
                                .split("/")
                                .mapNotNull { it.toIntOrNull() }
                                .toMutableList()

                        // 🔥 여기 숫자 두개랑 위에 // 바꾸면 됨
                        if (!myMedalList.contains(12)) {
                            myMedalList.add(12)

                            // 다시 문자열로 합치기
                            val updatedMedal = myMedalList.joinToString("/")

                            // DB 업데이트
                            userDao.update(
                                id = "etc",
                                value3 = updatedMedal
                            )

                            postSideEffect(BoardSideEffect.Toast("칭호를 획득했습니다!"))
                        }
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

    fun onAdClick() = intent {

        if(state.removeAd == "0") {
            postSideEffect(BoardSideEffect.ShowRewardAd)
        } else {
            onRewardEarned()
        }

    }

    fun showRewardAd(activity: Activity) {
        rewardAdManager.show(
            activity = activity,
            onReward = {
                onRewardEarned()
            },
            onNotReady = {
                intent {
                    postSideEffect(
                        BoardSideEffect.Toast(
                            "광고를 불러오는 중이에요. 잠시 후 다시 시도해주세요."
                        )
                    )
                }
            }
        )
    }

    private fun onRewardEarned() = intent {

        onBoardSubmitClick()

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
    val boardAnonymous: String = "0",
    val removeAd: String = "0"
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
    val anonymous: String = "0",
    val answerCount: Int = 0
)

//상태와 관련없는 것
sealed interface BoardSideEffect{
    class Toast(val message:String): BoardSideEffect
    data object NavigateToBoardMessageScreen: BoardSideEffect


    data object ShowRewardAd : BoardSideEffect

}