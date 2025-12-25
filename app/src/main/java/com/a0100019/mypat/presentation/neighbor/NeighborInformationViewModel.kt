package com.a0100019.mypat.presentation.neighbor

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
import com.a0100019.mypat.presentation.information.addMedalAction
import com.a0100019.mypat.presentation.information.getMedalActionCount
import com.a0100019.mypat.presentation.neighbor.chat.ChatMessage
import com.a0100019.mypat.presentation.neighbor.chat.ChatSideEffect
import com.a0100019.mypat.presentation.neighbor.community.CommunitySideEffect
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class NeighborInformationViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao
) : ViewModel(), ContainerHost<NeighborInformationState, NeighborInformationSideEffect> {

    override val container: Container<NeighborInformationState, NeighborInformationSideEffect> = container(
        initialState = NeighborInformationState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(NeighborInformationSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
    }

    //room에서 데이터 가져옴
    private fun loadData() = intent {
        val userDataList = userDao.getAllUserData()
        val patDataList = patDao.getAllPatData()
        val itemDataList = itemDao.getAllItemDataWithShadow()
        val allUserDataList = allUserDao.getAllUserDataNoBan()

        val allAreaCount = areaDao.getAllAreaData().size.toString()

        val clickUserTag = userDataList.find { it.id == "etc2" }!!.value3

        val selectedUser = allUserDataList
            .find { it.tag == clickUserTag }
            ?: AllUser(tag = clickUserTag) // 없으면 기본값

        val selectedUserWorldDataList = selectedUser.worldData
            .split("/")
            .filter { it.isNotBlank() }

        reduce {
            state.copy(
                userDataList = userDataList,
                patDataList = patDataList,
                itemDataList = itemDataList,
                allUserDataList =  allUserDataList,
                allAreaCount = allAreaCount,
                clickAllUserData = selectedUser,
                clickAllUserWorldDataList = selectedUserWorldDataList,
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

                        Firebase.firestore
                            .collection("code")
                            .document("ban")
                            .collection("ban")
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
                        Firebase.firestore
                            .collection("code")
                            .document("ban")
                            .collection("ban")
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
                                                postSideEffect(NeighborInformationSideEffect.Toast("이미 신고가 접수되었습니다."))
                                            }
                                            return@addOnSuccessListener  // 함수 조기 종료
                                        }

                                        time == messageData.timestamp
                                    } == true
                                } ?: false

                                // 🔐 ban 1스택이 있을 때만 실행
                                if (matched) {
                                    Firebase.firestore
                                        .collection("chatting")
                                        .document("totalChat")
                                        .collection("totalChat")
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

                                Firebase.firestore
                                    .collection("code")
                                    .document("ban")
                                    .collection("ban")
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
                        postSideEffect(NeighborInformationSideEffect.Toast("신고가 접수되었습니다"))
                    }

                }

            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "문서 가져오기 실패: ${e.message}")
            }

    }


    fun onPrivateChatStartClick() = intent {
        val myTag = state.userDataList.find { it.id == "auth" }!!.value2
        val yourTag = state.clickAllUserData.tag

        val myNum = myTag.toLongOrNull() ?: 0L
        val yourNum = yourTag.toLongOrNull() ?: 0L

        // 🔻 작은 숫자가 앞으로 오도록
        val docId = if (myNum < yourNum) "${myTag}_${yourTag}" else "${yourTag}_${myTag}"

        val docRef = Firebase.firestore
            .collection("chatting")
            .document("privateChat")
            .collection("privateChat")
            .document(docId)

        // 🔍 문서 존재 여부 확인
        docRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // 🔥 이미 방이 존재
                    viewModelScope.launch {
                        intent {
                            postSideEffect(NeighborInformationSideEffect.Toast("이미 친구입니다."))
                            postSideEffect(NeighborInformationSideEffect.NavigateToPrivateRoomScreen)

                        }
                    }
                    return@addOnSuccessListener
                }

                // 📌 participants 배열 생성
                val u1 = if (myNum < yourNum) myTag else yourTag
                val u2 = if (myNum < yourNum) yourTag else myTag

                // 📌 방 생성 데이터
                val chatInitData = mapOf(
                    "user1" to u1,
                    "user2" to u2,
                    "participants" to listOf(u1, u2),   // ⬅⬅⬅ 핵심 추가!
                    "createdAt" to System.currentTimeMillis(),
                    "last1" to System.currentTimeMillis(),
                    "last2" to System.currentTimeMillis(),
                    "lastMessage" to "",
                    "name1" to state.userDataList.find { it.id == "name" }!!.value,
                    "name2" to state.clickAllUserData.name,
                    "createUser" to state.userDataList.find { it.id == "auth" }!!.value,
                    "messageCount" to 0,
                    "attacker" to state.userDataList.find { it.id == "auth" }!!.value2,
                    "highScore" to 0,
                    "lastGame1" to "2001-01-01",
                    "lastGame2" to "2001-01-01",
                    "todayScore1" to 0,
                    "todayScore2" to 0,
                    "totalScore" to 0,
                )

                // 문서 생성
                docRef.set(chatInitData)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            intent {
                                postSideEffect(NeighborInformationSideEffect.Toast("친구를 맺었습니다"))
                                postSideEffect(NeighborInformationSideEffect.NavigateToPrivateRoomScreen)
                            }
                        }
                    }
                    .addOnFailureListener {
                        viewModelScope.launch {
                            intent {
                                postSideEffect(NeighborInformationSideEffect.Toast("친구 실패"))
                            }
                        }
                    }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    intent {
                        postSideEffect(NeighborInformationSideEffect.Toast("오류 발생"))
                    }
                }
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

                                                        var medalData = state.userDataList.find { it.id == "name" }!!.value2
                                                        medalData = addMedalAction(medalData, actionId = 11)
                                                        userDao.update(
                                                            id = "name",
                                                            value2 = medalData
                                                        )

                                                        if(getMedalActionCount(medalData, actionId = 11) >= 100) {
                                                            //매달, medal, 칭호11
                                                            val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

                                                            val myMedalList: MutableList<Int> =
                                                                myMedal
                                                                    .split("/")
                                                                    .mapNotNull { it.toIntOrNull() }
                                                                    .toMutableList()

                                                            // 🔥 여기 숫자 두개랑 위에 // 바꾸면 됨
                                                            if (!myMedalList.contains(11)) {
                                                                myMedalList.add(11)

                                                                // 다시 문자열로 합치기
                                                                val updatedMedal = myMedalList.joinToString("/")

                                                                // DB 업데이트
                                                                userDao.update(
                                                                    id = "etc",
                                                                    value3 = updatedMedal
                                                                )

                                                                postSideEffect(NeighborInformationSideEffect.Toast("칭호를 획득했습니다!"))
                                                            }
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
                                postSideEffect(NeighborInformationSideEffect.Toast("좋아요를 눌렀습니다"))
                            }
                        } else {
                            // 이미 존재할 때 Toast 띄우기
                            viewModelScope.launch {
                                postSideEffect(NeighborInformationSideEffect.Toast("이미 좋아요를 눌렀습니다"))
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

                                                    var medalData = state.userDataList.find { it.id == "name" }!!.value2
                                                    medalData = addMedalAction(medalData, actionId = 11)
                                                    userDao.update(
                                                        id = "name",
                                                        value2 = medalData
                                                    )

                                                    if(getMedalActionCount(medalData, actionId = 11) >= 100) {
                                                        //매달, medal, 칭호11
                                                        val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

                                                        val myMedalList: MutableList<Int> =
                                                            myMedal
                                                                .split("/")
                                                                .mapNotNull { it.toIntOrNull() }
                                                                .toMutableList()

                                                        // 🔥 여기 숫자 두개랑 위에 // 바꾸면 됨
                                                        if (!myMedalList.contains(11)) {
                                                            myMedalList.add(11)

                                                            // 다시 문자열로 합치기
                                                            val updatedMedal = myMedalList.joinToString("/")

                                                            // DB 업데이트
                                                            userDao.update(
                                                                id = "etc",
                                                                value3 = updatedMedal
                                                            )

                                                            postSideEffect(NeighborInformationSideEffect.Toast("칭호를 획득했습니다!"))
                                                        }
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
                            postSideEffect(NeighborInformationSideEffect.Toast("좋아요를 눌렀습니다 +1000달빛"))
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error accessing community document", e)
                    viewModelScope.launch {
                        postSideEffect(NeighborInformationSideEffect.Toast("인터넷 오류"))
                    }
                }

            loadData()
        } else {
            postSideEffect(NeighborInformationSideEffect.Toast("좋아요는 내일부터 누를 수 있습니다"))
        }
    }

    fun onSituationChange(situation: String) = intent {
        reduce {
            state.copy(
                situation = situation
            )
        }
    }


}

@Immutable
data class NeighborInformationState(
    val userDataList: List<User> = emptyList(),
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val allUserDataList: List<AllUser> = emptyList(),
    val situation: String = "world",
    val clickAllUserData: AllUser = AllUser(),
    val clickAllUserWorldDataList: List<String> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val alertState: String = "",
    val allAreaCount: String = "",

    )

//상태와 관련없는 것
sealed interface NeighborInformationSideEffect{
    class Toast(val message:String): NeighborInformationSideEffect
    data object NavigateToPrivateRoomScreen: NeighborInformationSideEffect

}