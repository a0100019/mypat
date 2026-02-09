package com.a0100019.mypat.presentation.neighbor.board

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
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
import com.a0100019.mypat.data.room.photo.Photo
import com.a0100019.mypat.data.room.photo.PhotoDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.world.WorldDao
import com.a0100019.mypat.presentation.diary.DiaryWriteSideEffect
import com.a0100019.mypat.presentation.main.management.RewardAdManager
import com.a0100019.mypat.presentation.main.management.addMedalAction
import com.a0100019.mypat.presentation.main.management.getMedalActionCount
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean
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
    private val rewardAdManager: RewardAdManager,
    private val photoDao: PhotoDao,
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
                photoFirebaseUrl = "0",
                photoLocalPath = "0"
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
            .limit(50)
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
                        answerCount = (data["answer"] as? Map<*, *>)?.size ?: 0,
                        photoFirebaseUrl = data["photoFirebaseUrl"] as? String ?: "0",
                        photoLocalPath = data["photoLocalPath"] as? String ?: "0",
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
                                anonymous = data["anonymous"] as? String ?: "0",
                                answerCount = (data["answer"] as? Map<*, *>)?.size ?: 0,
                                photoFirebaseUrl = data["photoFirebaseUrl"] as? String ?: "0",
                                photoLocalPath = data["photoLocalPath"] as? String ?: "0",
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

        // 🔒 이미 전송 중이면 무시
        if (state.isSubmitting) return@intent

        val currentMessage = state.text.trim()

        if (currentMessage.length < 5) {
            postSideEffect(BoardSideEffect.Toast("5자 이상 입력해주세요."))
            return@intent
        }

        // 🔒 전송 시작
        reduce {
            state.copy(isSubmitting = true)
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
            "like" to 0,
            "type" to state.boardType,
            "anonymous" to state.boardAnonymous,
            "photoFirebaseUrl" to state.photoFirebaseUrl,
            "photoLocalPath" to state.photoLocalPath
        )

        Firebase.firestore
            .collection("chatting")
            .document("board")
            .collection("board")
            .document(timestamp.toString())
            .set(boardData)
            .addOnSuccessListener {

                viewModelScope.launch {
                    reduce {
                        state.copy(
                            situation = "boardSubmitConfirm",
                            isSubmitting = false // ✅ 해제
                        )
                    }

                    /* ---- 이하 네 기존 메달 로직 그대로 ---- */
                    var medalData =
                        userDao.getAllUserData().find { it.id == "name" }!!.value2
                    medalData = addMedalAction(medalData, actionId = 12)
                    userDao.update(id = "name", value2 = medalData)

                    if (getMedalActionCount(medalData, actionId = 12) >= 1) {
                        val myMedal =
                            userDao.getAllUserData().find { it.id == "etc" }!!.value3

                        val myMedalList = myMedal
                            .split("/")
                            .mapNotNull { it.toIntOrNull() }
                            .toMutableList()

                        if (!myMedalList.contains(12)) {
                            myMedalList.add(12)
                            userDao.update(
                                id = "etc",
                                value3 = myMedalList.joinToString("/")
                            )
                            postSideEffect(BoardSideEffect.Toast("칭호를 획득했습니다!"))
                        }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    // ❌ 실패 시도 다시 가능
                    reduce {
                        state.copy(isSubmitting = false)
                    }
                    postSideEffect(BoardSideEffect.Toast("작성 실패"))
                }
            }
    }


//    fun onAdClick() = intent {
//
//        if(state.removeAd == "0") {
//            postSideEffect(BoardSideEffect.ShowRewardAd)
//        } else {
//            onRewardEarned()
//        }
//
//    }

//    fun showRewardAd(activity: Activity) {
//        rewardAdManager.show(
//            activity = activity,
//            onReward = {
//                onRewardEarned()
//            },
//            onNotReady = {
//                intent {
//                    postSideEffect(
//                        BoardSideEffect.Toast(
//                            "광고를 불러오는 중이에요. 잠시 후 다시 시도해주세요."
//                        )
//                    )
//                }
//            }
//        )
//    }
//
//    private fun onRewardEarned() = intent {
//
//        onBoardSubmitClick()
//
//    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        val isAvailable = capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )

        Log.d("NetworkCheck", "Is Network Available: $isAvailable") // 로그 추가
        return isAvailable
    }

    fun handleImageSelection(context: Context, uri: Uri) = intent {
        // 1. 상태 초기화
        reduce { state.copy(isPhotoLoading = true) }

        val isUploadFinished = AtomicBoolean(false)
        val isAdClosed = AtomicBoolean(false)

        // 로딩 종료 여부를 판단하는 함수
        fun tryFinishLoading() = intent {
            if (isUploadFinished.get() && isAdClosed.get()) {
                reduce { state.copy(isPhotoLoading = false) }
            }
        }

        // --- 광고 분기 처리 로직 ---
        // 기존에 사진이 하나라도 있으면(photoDataList가 비어있지 않으면) 광고를 띄움
        val shouldShowAd = state.photoDataList.isNotEmpty()

        if (false) {
            // [광고를 보여주는 경우]
//            postSideEffect(DiaryWriteSideEffect.ShowInterstitialAd {
//                isAdClosed.set(true)
//                tryFinishLoading()
//            })
        } else {
            // [첫 사진이라 광고를 안 보여주는 경우]
            // 광고가 이미 닫힌 것으로 간주하여 true로 설정
            isAdClosed.set(true)
            // tryFinishLoading은 호출할 필요 없음 (업로드 끝나면 알아서 종료됨)
        }

        // 2. [병렬 실행] 이미지 처리 및 업로드 (백그라운드)
        viewModelScope.launch(Dispatchers.IO) {
            if (!isNetworkAvailable(context)) {
                postSideEffect(BoardSideEffect.Toast("인터넷 연결을 확인해주세요."))
                isUploadFinished.set(true)
                tryFinishLoading()
                return@launch
            }

            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val localPath = saveImageToInternalStorage(context, uri)
            if (localPath != null) {
                uploadToFirebase(localPath) { firebaseUrl ->
                    intent {
                        if (firebaseUrl != null) {
                            viewModelScope.launch(Dispatchers.IO) {
                                val photoEntry = Photo(
                                    date = today,
                                    localPath = localPath,
                                    firebaseUrl = firebaseUrl,
                                    isSynced = false
                                )
                                photoDao.insert(photoEntry)
                                val updatedPhotos = photoDao.getUnsyncedPhotosByDate(today)

                                intent {
                                    reduce {
                                        state.copy(
                                            photoDataList = updatedPhotos,
                                            photoFirebaseUrl = firebaseUrl,
                                            photoLocalPath = localPath
                                        )
                                    }
                                    isUploadFinished.set(true)
                                    tryFinishLoading()
                                }
                            }
                        } else {
                            File(localPath).delete()
                            postSideEffect(BoardSideEffect.Toast("업로드 실패"))
                            isUploadFinished.set(true)
                            tryFinishLoading()
                        }
                    }
                }
            } else {
                postSideEffect(BoardSideEffect.Toast("저장 오류"))
                isUploadFinished.set(true)
                tryFinishLoading()
            }
        }
    }

    private fun uploadToFirebase(localPath: String, onComplete: (String?) -> Unit) {
        val user = Firebase.auth.currentUser ?: return onComplete(null)

        val file = File(localPath)
        if (!file.exists()) return onComplete(null)

        val storageRef = Firebase.storage.reference
        // 보안을 위해 파일명 앞에 UID를 섞거나 랜덤값을 추가하는 것이 좋습니다.
        val imageRef = storageRef.child("board/${file.name}")

        // 파일 읽기 -> XOR 뒤섞기 -> 업로드
        try {
            val compressedBytes = file.readBytes()
            val scrambledBytes = togglePrivacy(compressedBytes)

            imageRef.putBytes(scrambledBytes)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        onComplete(uri.toString())
                    }
                }
                .addOnFailureListener {
                    onComplete(null)
                }
        } catch (e: Exception) {
            onComplete(null)
        }
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val fileName = "haru_photo_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            // 1. InputStream으로 비트맵 불러오기
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // 2. 파일 출력 스트림 준비
                FileOutputStream(file).use { outputStream ->
                    // 3. 압축하기 (JPEG, 품질 70~80% 권장)
                    // 품질을 100에서 80으로만 낮춰도 용량이 획기적으로 줄어듭니다.
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    private fun togglePrivacy(data: ByteArray): ByteArray {
        val key = 0xAF.toByte()
        return ByteArray(data.size) { i -> (data[i].toInt() xor key.toInt()).toByte() }
    }

    fun deleteImage(photo: Photo) = intent {
        // 1. 비동기 작업으로 파일과 DB 데이터 삭제
        val isDeleted = withContext(Dispatchers.IO) {
            try {
                // (1) 내부 저장소에서 실제 파일 삭제
                val file = File(photo.localPath)
                if (file.exists()) {
                    file.delete()
                }

                // (2) DB에서 해당 포토 엔티티 삭제
                photoDao.delete(photo) // DAO에 delete 메서드가 있다고 가정합니다.
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        if (isDeleted) {
            // 2. 최신 리스트로 UI 상태 갱신
            val updatedList = photoDao.getUnsyncedPhotosByDate(today)
            reduce {
                state.copy(
                    photoDataList = updatedList,
                    photoLocalPath = "0",
                    photoFirebaseUrl = "0"
                )
            }
        } else {
            // 실패 시 에러 처리 (선택 사항)
            // postSideEffect(DiaryWriteSideEffect.ShowToast("사진 삭제에 실패했습니다."))
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
    val boardAnonymous: String = "0",
    val removeAd: String = "0",
    val isSubmitting: Boolean = false,

    val photoDataList: List<Photo> = emptyList(),
    val isPhotoLoading: Boolean = false, // 로딩 상태 추가
    val photoFirebaseUrl: String = "0",
    val photoLocalPath: String = "0"
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
    val answerCount: Int = 0,
    val photoFirebaseUrl: String = "0",
    val photoLocalPath: String = "0"
)

//상태와 관련없는 것
sealed interface BoardSideEffect{
    class Toast(val message:String): BoardSideEffect
    data object NavigateToBoardMessageScreen: BoardSideEffect

//    data object ShowRewardAd : BoardSideEffect

}