package com.a0100019.mypat.presentation.neighbor.board

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.photo.Photo
import com.a0100019.mypat.data.room.photo.PhotoDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.world.WorldDao
import com.a0100019.mypat.presentation.main.management.addMedalAction
import com.a0100019.mypat.presentation.main.management.getMedalActionCount
import com.a0100019.mypat.presentation.neighbor.community.CommunitySideEffect
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class BoardMessageViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao,
    private val photoDao: PhotoDao,
    @ApplicationContext private val context: Context
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
        loadBoardMessage()
    }

    //room에서 데이터 가져옴
    private fun loadData() = intent {

        val userDataList = userDao.getAllUserData()

        reduce {
            state.copy(
                userDataList = userDataList
            )
        }

        // 1. 사진 데이터를 가져오고 업데이트하는 메인 로직
        // 사진 데이터 로드 및 업데이트 로직
        val photoDataList = photoDao.getPhotoByPath(state.boardData.photoLocalPath)

        if(state.boardData.photoLocalPath != "0"){
            if (photoDataList != null) {
                reduce {
                    state.copy(
                        photoDataList = listOf(photoDataList)
                    )
                }
                Log.e("BoardMessageViewModel", "사진 데이터 로드 성공")
            } else {
                viewModelScope.launch {
                    val firebaseUrl = state.boardData.photoFirebaseUrl
                    val currentLocalPath = state.boardData.photoLocalPath

                    // 로딩 시작
                    reduce { state.copy(isPhotoLoading = true) }

                    try {
                        // 1. 이미지 다운로드
                        val newLocalPath =
                            downloadImageToLocal(context, firebaseUrl, currentLocalPath)
                        val today =
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                        if (newLocalPath != null) {
                            val newPhoto = Photo(
                                date = today,
                                firebaseUrl = firebaseUrl,
                                localPath = newLocalPath,
                                isSynced = false
                            )

                            // 2. DB에 저장
                            photoDao.insert(newPhoto)

                            // 3. State 업데이트 및 로딩 종료
                            reduce {
                                state.copy(
                                    boardData = state.boardData.copy(
                                        photoLocalPath = newLocalPath,
                                        photoFirebaseUrl = firebaseUrl
                                    ),
                                    photoDataList = listOf(newPhoto),
                                    isPhotoLoading = false // 성공 시 로딩 종료
                                )
                            }
                            Log.e("BoardMessageViewModel", "사진 데이터 없어서 받아옴 성공")
                        } else {
                            // 다운로드 실패 시에도 로딩은 꺼줘야 함
                            reduce { state.copy(isPhotoLoading = false) }
                            Log.e("BoardMessageViewModel", "사진 다운로드 실패")
                        }
                    } catch (e: Exception) {
                        // 에러 발생 시 로딩 종료
                        reduce { state.copy(isPhotoLoading = false) }
                        Log.e("BoardMessageViewModel", "에러 발생: ${e.message}")
                    }
                }
            }
        }

    }

    private suspend fun downloadImageToLocal(context: Context, firebaseUrl: String, localPath: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // localPath가 이미 "/data/user/0/.../haru_photo_17706549.jpg" 라면
                // 이를 바로 File 객체로 만듭니다.
                val localFile = File(localPath)

                // 부모 폴더가 없을 경우 대비 (보안)
                localFile.parentFile?.mkdirs()

                val storageRef = Firebase.storage.getReferenceFromUrl(firebaseUrl)
                storageRef.getFile(localFile).await()

                // 복호화 로직
                val originalBytes = togglePrivacy(localFile.readBytes())
                localFile.writeBytes(originalBytes)

                localFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun togglePrivacy(data: ByteArray): ByteArray {
        val key = 0xAF.toByte()
        return ByteArray(data.size) { i -> (data[i].toInt() xor key.toInt()).toByte() }
    }


    fun onClose() = intent {
        reduce {
            state.copy(
                situation = "",
                text = ""
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
             * 1️⃣ boardData
             * --------------------------- */
            val boardData = BoardMessage(
                timestamp = boardTimestamp.toLong(),
                message = snap.getString("message") ?: "",
                name = snap.getString("name") ?: "",
                tag = snap.getString("tag") ?: "",
                uid = snap.getString("uid") ?: "",
                type = snap.getString("type") ?: "",
                anonymous = snap.getString("anonymous") ?: "",
                photoFirebaseUrl = snap.getString("photoFirebaseUrl") ?: "0",
                photoLocalPath = snap.getString("photoLocalPath") ?: "0"
            )

            /* ---------------------------
             * 2️⃣ boardChat (answer 맵)
             * --------------------------- */
            val boardChatList = mutableListOf<BoardChatMessage>()

            val answerMap = snap.get("answer") as? Map<*, *> ?: emptyMap<Any, Any>()

            for ((timestampKey, value) in answerMap) {

                val timestamp = timestampKey.toString().toLongOrNull() ?: continue
                val map = value as? Map<*, *> ?: continue

                val ban = map["ban"] as? String ?: "0"
                if (ban == "1") continue   // 🔥 차단된 답글 제외

                boardChatList.add(
                    BoardChatMessage(
                        timestamp = timestamp,
                        message = map["message"] as? String ?: "",
                        name = map["name"] as? String ?: "",
                        tag = map["tag"] as? String ?: "",
                        ban = ban,
                        uid = map["uid"] as? String ?: "",
                        anonymous = map["anonymous"] as? String ?: ""
                    )
                )
            }

            val sortedChat = boardChatList.sortedBy { it.timestamp }

            intent {
                reduce {
                    state.copy(
                        boardData = boardData,
                        boardChat = sortedChat,
                    )
                }
            }

            loadData()
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

    fun onBoardDelete() = intent {

        val userDataList = userDao.getAllUserData()
        val boardTimestamp =
            userDataList.find { it.id == "etc2" }?.value3 ?: return@intent

        val boardRef = Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")
            .document(boardTimestamp)

        boardRef
            .delete()
            .addOnSuccessListener {
                Log.d("BoardDelete", "게시글 삭제 성공")

                // 필요하면 상태 초기화
                viewModelScope.launch {
                    intent {
                        reduce {
                            state.copy(
                                situation = "deleteCheck"
                            )
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BoardDelete", "게시글 삭제 실패: ${e.message}")
            }
    }

    fun onBoardChatDelete(commentTimestamp: String) = intent {

        val userDataList = userDao.getAllUserData()

        val boardTimestamp =
            userDataList.find { it.id == "etc2" }!!.value3  // 게시글 문서명

        Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")
            .document(boardTimestamp)
            .update(
                mapOf(
                    "answer.$commentTimestamp" to FieldValue.delete()
                )
            )
            .addOnSuccessListener {
                Log.d("BoardChatDelete", "댓글 삭제 성공")
            }
            .addOnFailureListener { e ->
                Log.e("BoardChatDelete", "댓글 삭제 실패: ${e.message}")
            }
    }

    fun onNeighborInformationClick(neighborTag: String) = intent {

        userDao.update(id = "etc2", value3 = neighborTag)
        postSideEffect(BoardMessageSideEffect.NavigateToNeighborInformationScreen)

    }

    fun clickPhotoChange(path: String) = intent {
        reduce {
            state.copy(
                clickPhoto = path
            )
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

    val isPhotoLoading: Boolean = false, // 로딩 상태 추가
    val clickPhoto: String = "",
    val photoDataList: List<Photo> = emptyList(),
    )

@Immutable
data class BoardChatMessage(
    val timestamp: Long = 0L,
    val message: String = "0",
    val name: String = "0",
    val tag: String = "0",
    val ban: String = "0",
    val uid: String = "0",
    val anonymous: String = "0",
    val photoFirebaseUrl: String = "0",
    val photoLocalPath: String = "0"
)


//상태와 관련없는 것
sealed interface BoardMessageSideEffect{
    class Toast(val message:String): BoardMessageSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

    data object NavigateToNeighborInformationScreen: BoardMessageSideEffect

}