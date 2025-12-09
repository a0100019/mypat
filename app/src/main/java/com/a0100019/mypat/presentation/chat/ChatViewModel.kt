package com.a0100019.mypat.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.world.WorldDao
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao
) : ViewModel(), ContainerHost<ChatState, ChatSideEffect> {

    override val container: Container<ChatState, ChatSideEffect> = container(
        initialState = ChatState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(ChatSideEffect.Toast(message = throwable.message.orEmpty()))
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
        val patDataList = patDao.getAllPatData()
        val itemDataList = itemDao.getAllItemDataWithShadow()
        var allUserDataList = allUserDao.getAllUserDataNoBan()
        allUserDataList = allUserDataList.filter { it.totalDate != "1" && it.totalDate != "0" }

        val allAreaCount = areaDao.getAllAreaData().size.toString()

        if(allUserDataList.isEmpty()) {
            reduce {
                state.copy(
                    situation = "update"
                )
            }
        }

        val currentDate =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        if(currentDate != userDao.getValue2ById("etc") ){

            reduce {
                state.copy(
                    situation = "update"
                )
            }

        }

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

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                dialogState = "",
                newChat = "",
                text2 = "",
                text3 = ""
            )
        }
    }

    fun onDialogChangeClick(dialog: String) = intent {

        reduce {
            state.copy(
                dialogState = dialog
            )
        }

    }

    fun onAskSubmitClick() = intent {
        val currentMessage = state.newChat.trim()
        val userName = state.userDataList.find { it.id == "name" }!!.value // 또는 상태에서 유저 이름을 가져올 수 있다면 사용
        val userId = state.userDataList.find { it.id == "auth" }!!.value
        val userTag = state.userDataList.find { it.id == "auth" }!!.value2
        val userBan = state.userDataList.find { it.id == "community" }!!.value3

        if (currentMessage.isEmpty()) return@intent

        val timestamp = System.currentTimeMillis()
        val todayDocId = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        val chatData = mapOf(
            "message" to currentMessage,
            "name" to userName,
            "ban" to userBan,
            "tag" to userTag,
            "uid" to userId
        )

        Firebase.firestore.collection("ask")
            .document(todayDocId)
            .set(mapOf(timestamp.toString() to chatData), SetOptions.merge())
            .addOnSuccessListener {
                Log.d("ChatSubmit", "채팅 전송 성공 (merge)")
                viewModelScope.launch {
                    postSideEffect(ChatSideEffect.Toast("도란도란을 전송했습니다 :)"))
                }
            }
            .addOnFailureListener { e ->
                Log.e("ChatSubmit", "채팅 전송 실패: ${e.message}")
                viewModelScope.launch {
                    postSideEffect(ChatSideEffect.Toast("전송에 실패했습니다."))
                }
            }

        // 입력 필드 초기화
        reduce {
            state.copy(
                newChat = "",
                dialogState = ""
            )
        }
    }


    fun onAskClick(message: String) = intent {
        reduce {
            state.copy(
                newChat = message,
                dialogState = "askWrite"
            )
        }
    }

    private fun loadChatMessages() {
        Firebase.firestore.collection("chat")
            .addSnapshotListener { snapshot, error ->
                Log.d("CommunityViewModel", "전체 채팅 스냅샷 수신됨")

                if (error != null) {
                    Log.e("CommunityViewModel", "채팅 데이터 에러: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val allMessages = mutableListOf<ChatMessage>()

                    for (doc in snapshot.documents) {

                        val data = doc.data ?: continue
                        val messages = data.mapNotNull { (key, value) ->
                            val timestamp = key.toLongOrNull() ?: return@mapNotNull null
                            val map = value as? Map<*, *> ?: return@mapNotNull null
                            val message = map["message"] as? String
                            val name = map["name"] as? String
                            val tag = map["tag"] as? String
                            val ban = map["ban"] as? String
                            val uid = map["uid"] as? String

                            if (message != null && name != null && tag != null && ban == "0" && uid != null) {
                                ChatMessage(timestamp, message, name, tag, ban, uid)
                            } else null
                        }
                        allMessages.addAll(messages)
                    }

                    val sorted = allMessages.sortedBy { it.timestamp }

                    viewModelScope.launch {
                        intent {
                            reduce {
                                state.copy(chatMessages = sorted)
                            }
                        }
                    }
                } else {
                    Log.w("CommunityViewModel", "chat 컬렉션에 문서가 없음")
                }
            }
    }

    fun onSituationChange(newSituation: String) = intent {
        reduce {

            state.copy(
                situation = newSituation,
            )
        }
    }

    fun onUserRankClick(userTag: Int) = intent {
        if(userTag == 0){
            reduce {
                state.copy(
                    clickAllUserData = AllUser(),
                    clickAllUserWorldDataList = emptyList()
                )
            }
        } else {
            val selectedUser = state.allUserDataList.find { it.tag == userTag.toString() }
            val selectedUserWorldDataList: List<String> = selectedUser!!.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거

            reduce {
                state.copy(
                    clickAllUserData = selectedUser,
                    clickAllUserWorldDataList = selectedUserWorldDataList
                )
            }
        }
    }

    fun onChatSubmitClick() = intent {
        val currentMessage = state.newChat.trim()
        val userName = state.userDataList.find { it.id == "name" }!!.value // 또는 상태에서 유저 이름을 가져올 수 있다면 사용
        val userId = state.userDataList.find { it.id == "auth" }!!.value
        val userTag = state.userDataList.find { it.id == "auth" }!!.value2
        val userBan = state.userDataList.find { it.id == "community" }!!.value3

        if (currentMessage.isEmpty()) return@intent

        val timestamp = System.currentTimeMillis()
        val todayDocId = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        val chatData = mapOf(
            "message" to currentMessage,
            "name" to userName,
            "ban" to userBan,
            "tag" to userTag,
            "uid" to userId
        )

        Firebase.firestore.collection("chat")
            .document(todayDocId)
            .set(mapOf(timestamp.toString() to chatData), SetOptions.merge())
            .addOnSuccessListener {
                Log.d("ChatSubmit", "채팅 전송 성공 (merge)")
            }
            .addOnFailureListener { e ->
                Log.e("ChatSubmit", "채팅 전송 실패: ${e.message}")
            }

        // 입력 필드 초기화
        reduce {
            state.copy(newChat = "")
        }
    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onChatTextChange(chatText: String) = blockingIntent {

//        if (chatText.length <= 50) {
        reduce {
            state.copy(newChat = chatText)
        }
//        }
    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onTextChange2(text2: String) = blockingIntent {

//        if (chatText.length <= 50) {
        reduce {
            state.copy(text2 = text2)
        }
//        }
    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onTextChange3(text3: String) = blockingIntent {

//        if (chatText.length <= 50) {
        reduce {
            state.copy(text3 = text3)
        }
//        }
    }

    fun onBanClick(chatIndex: Int) = intent {

        //신고자 UID
        val fromUID = state.userDataList.find { it.id == "auth" }!!.value
        //오늘 날짜
        val todayDocId = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        Firebase.firestore
            .collection("users")
            .document(fromUID)
            .get()
            .addOnSuccessListener { document ->
                val communityMap = document.get("community") as? Map<*, *>
                val warningValue = communityMap?.get("warning") as? String

                if (warningValue == "0") {
                    // 🔽 warning 값이 "0"일 때 실행 되는 코드 = 0이 아니면 신고를 많이해서 막아놓은 것
                    Log.d("Firestore", "warning = 0 -> 처리 실행")
                    // 원하는 작업 수행

                    //world신고
                    if (chatIndex == -1) {

                        val banData = mapOf(
                            System.currentTimeMillis().toString() to mapOf(
                                "fromUID" to fromUID,
                                "name" to state.clickAllUserData.name,
                            )

                        )

                        Firebase.firestore.collection("ban")
                            .document(todayDocId)
                            .set(mapOf(state.clickAllUserData.tag to banData), SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d("BanSubmit", "벤 전송 성공 (merge)")
                            }
                            .addOnFailureListener { e ->
                                Log.e("BanSubmit", "벤 전송 실패: ${e.message}")
                            }

                    } else { // 채팅 신고
                        val messageData = state.chatMessages[state.chatMessages.lastIndex - chatIndex]
                        // Step 1: ban 컬렉션 확인
                        Firebase.firestore.collection("ban")
                            .document(todayDocId)
                            .get()
                            .addOnSuccessListener { banSnapshot ->
                                val banData = banSnapshot.data

                                val matched = banData?.any { (_, nestedMap) ->
                                    (nestedMap as? Map<*, *>)?.values?.any { value ->
                                        val map = value as? Map<*, *>
                                        val time = map?.get("time") as? Long
                                        val firstFromUID = map?.get("fromUID") as? String

                                        if (time == messageData.timestamp && firstFromUID == fromUID) {
                                            viewModelScope.launch {
                                                postSideEffect(ChatSideEffect.Toast("이미 신고가 접수되었습니다."))
                                            }
                                            return@addOnSuccessListener  // 함수 조기 종료
                                        }

                                        time == messageData.timestamp
                                    } == true
                                } ?: false

                                // 🔐 ban 1스택이 있을 때만 실행
                                if (matched) {
                                    Firebase.firestore.collection("chat")
                                        .document(todayDocId)
                                        .update(
                                            messageData.timestamp.toString() + ".ban", "1"
                                        )
                                        .addOnSuccessListener {
                                            Log.d("ChatUpdate", "ban 값 업데이트 성공")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("ChatUpdate", "ban 업데이트 실패: ${e.message}")
                                        }
                                }

                                // 2. ban 컬렉션에 추가
                                val banDataToSend = mapOf(
                                    System.currentTimeMillis().toString() to mapOf(
                                        "fromUID" to fromUID,
                                        "message" to messageData.message,
                                        "name" to state.clickAllUserData.name,
                                        "time" to messageData.timestamp
                                    )
                                )

                                Firebase.firestore.collection("ban")
                                    .document(todayDocId)
                                    .set(mapOf(state.clickAllUserData.tag to banDataToSend), SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d("BanSubmit", "벤 전송 성공 (merge)")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("BanSubmit", "벤 전송 실패: ${e.message}")
                                    }

                            }
                            .addOnFailureListener { e ->
                                Log.e("BanCheck", "ban 문서 불러오기 실패: ${e.message}")
                            }
                    }

                    viewModelScope.launch {
                        postSideEffect(ChatSideEffect.Toast("신고가 접수되었습니다"))
                    }

                }

            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "문서 가져오기 실패: ${e.message}")
            }

    }

    fun alertStateChange(alertState: String) = intent {
        reduce {
            state.copy(
                alertState = alertState
            )
        }
    }

    fun onLikeClick() = intent {

        if(state.userDataList.find { it.id == "date" }!!.value2 != "1"){
            val db = Firebase.firestore
            val myUid = state.userDataList.find { it.id == "auth" }!!.value
            val today =
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) // "20250516"
            val docRef =
                db.collection("users").document(myUid).collection("community").document(today)
            val tag = state.clickAllUserData.tag

            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val likeList = documentSnapshot.get("like") as? List<String> ?: emptyList()

                        //오늘 좋아요를 누르지 않은 사람
                        if (!likeList.contains(tag)) {
                            //FieldValue.arrayUnion(...): Firestore에서 배열에 중복 없이 값 추가할 때 사용.
                            docRef.update("like", FieldValue.arrayUnion(tag))

                            Firebase.firestore.collection("users")
                                .whereEqualTo("tag", tag)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val document = querySnapshot.documents.firstOrNull()

                                    if (document != null) {
                                        val community = document.get("community") as? Map<*, *>
                                        val likeValueStr = community?.get("like")?.toString()

                                        // 숫자로 변환 시도
                                        val likeValue = likeValueStr?.toIntOrNull()

                                        if (likeValue != null) {
                                            val newLikeValue = likeValue + 1
                                            val updatedCommunity = community.toMutableMap()
                                            updatedCommunity["like"] = newLikeValue.toString()

                                            document.reference.update("community", updatedCommunity)
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        "TAG",
                                                        "like 값이 $likeValue → $newLikeValue 으로 업데이트됨"
                                                    )
                                                    viewModelScope.launch {
                                                        allUserDao.updateLikeByTag(
                                                            tag = tag,
                                                            newLike = newLikeValue.toString()
                                                        )
                                                        reduce {
                                                            state.copy(
                                                                clickAllUserData = state.clickAllUserData.copy(
                                                                    like = (state.clickAllUserData.like.toInt() + 1).toString()
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e("TAG", "업데이트 실패: ${e.message}")
                                                }
                                        } else {
                                            Log.w("TAG", "like 필드가 숫자가 아닙니다: $likeValueStr")
                                        }
                                    } else {
                                        Log.w("TAG", "해당 태그를 가진 문서를 찾을 수 없습니다.")
                                        viewModelScope.launch {
                                            allUserDao.updateLikeByTag(
                                                tag = tag,
                                                newLike = (state.clickAllUserData.like.toInt() + 1).toString()
                                            )
                                            reduce {
                                                state.copy(
                                                    clickAllUserData = state.clickAllUserData.copy(
                                                        like = (state.clickAllUserData.like.toInt() + 1).toString()
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("TAG", "문서 가져오기 실패: ${e.message}")
                                }

                            viewModelScope.launch {
                                postSideEffect(ChatSideEffect.Toast("좋아요를 눌렀습니다"))
                            }
                        } else {
                            // 이미 존재할 때 Toast 띄우기
                            viewModelScope.launch {
                                postSideEffect(ChatSideEffect.Toast("이미 좋아요를 눌렀습니다"))
                            }
                        }
                    } else {
                        //오늘 첫 좋아요
                        val newData = hashMapOf(
                            "like" to listOf(tag)
                        )
                        docRef.set(newData)

                        Firebase.firestore.collection("users")
                            .whereEqualTo("tag", tag)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val document = querySnapshot.documents.firstOrNull()

                                if (document != null) {
                                    val community = document.get("community") as? Map<*, *>
                                    val likeValueStr = community?.get("like")?.toString()

                                    // 숫자로 변환 시도
                                    val likeValue = likeValueStr?.toIntOrNull()

                                    if (likeValue != null) {
                                        val newLikeValue = likeValue + 1
                                        val updatedCommunity = community.toMutableMap()
                                        updatedCommunity["like"] = newLikeValue.toString()

                                        document.reference.update("community", updatedCommunity)
                                            .addOnSuccessListener {

                                                Log.d(
                                                    "TAG",
                                                    "like 값이 $likeValue → $newLikeValue 으로 업데이트됨"
                                                )
                                                viewModelScope.launch {
                                                    allUserDao.updateLikeByTag(
                                                        tag = tag,
                                                        newLike = newLikeValue.toString()
                                                    )

                                                    reduce {
                                                        state.copy(
                                                            clickAllUserData = state.clickAllUserData.copy(
                                                                like = (state.clickAllUserData.like.toInt() + 1).toString()
                                                            )
                                                        )
                                                    }

                                                    userDao.update(
                                                        id = "money",
                                                        value2 = (state.userDataList.find { it.id == "money" }!!.value2.toInt() + 1000).toString()
                                                    )
                                                }

                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("TAG", "업데이트 실패: ${e.message}")
                                            }
                                    } else {
                                        Log.w("TAG", "like 필드가 숫자가 아닙니다: $likeValueStr")
                                    }
                                } else {
                                    Log.w("TAG", "해당 태그를 가진 문서를 찾을 수 없습니다.")
                                    viewModelScope.launch {
                                        allUserDao.updateLikeByTag(
                                            tag = tag,
                                            newLike = (state.clickAllUserData.like.toInt() + 1).toString()
                                        )
                                        reduce {
                                            state.copy(
                                                clickAllUserData = state.clickAllUserData.copy(
                                                    like = (state.clickAllUserData.like.toInt() + 1).toString()
                                                )
                                            )
                                        }

                                        userDao.update(
                                            id = "money",
                                            value2 = (state.userDataList.find { it.id == "money" }!!.value2.toInt() + 1000).toString()
                                        )

                                    }

                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("TAG", "문서 가져오기 실패: ${e.message}")
                            }


                        viewModelScope.launch {
                            postSideEffect(ChatSideEffect.Toast("좋아요를 눌렀습니다 +1000달빛"))
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error accessing community document", e)
                    viewModelScope.launch {
                        postSideEffect(ChatSideEffect.Toast("인터넷 오류"))
                    }
                }

            loadData()
        } else {
            postSideEffect(ChatSideEffect.Toast("좋아요는 내일부터 누를 수 있습니다"))
        }
    }

}

@Immutable
data class ChatState(
    val userDataList: List<User> = emptyList(),
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val allUserDataList: List<AllUser> = emptyList(),
    val situation: String = "world",
    val clickAllUserData: AllUser = AllUser(),
    val clickAllUserWorldDataList: List<String> = emptyList(),
    val allUserRankDataList: List<AllUser> = emptyList(),
    val newChat: String = "",
    val chatMessages: List<ChatMessage> = emptyList(),
    val alertState: String = "",
    val allAreaCount: String = "",
    val dialogState: String = "",
    val text2: String = "",
    val text3: String = "",
)

@Immutable
data class ChatMessage(
    val timestamp: Long,
    val message: String,
    val name: String,
    val tag: String,
    val ban: String,
    val uid: String
)


//상태와 관련없는 것
sealed interface ChatSideEffect{
    class Toast(val message:String): ChatSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}