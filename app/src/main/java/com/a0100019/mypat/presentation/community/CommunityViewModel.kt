package com.a0100019.mypat.presentation.community

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
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
class CommunityViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao
) : ViewModel(), ContainerHost<CommunityState, CommunitySideEffect> {

    override val container: Container<CommunityState, CommunitySideEffect> = container(
        initialState = CommunityState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(CommunitySideEffect.Toast(message = throwable.message.orEmpty()))
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
        val itemDataList = itemDao.getAllItemData()
        val allUserDataList = allUserDao.getAllUserData()
        val allUserRankDataList = allUserDao.getAllUserData()

        val page = userDataList.find { it.id == "etc" }!!.value.toInt()
        val allUserData1 = allUserDataList[4*page]
        val allUserData2 = allUserDataList[4*page + 1]
        val allUserData3 = allUserDataList[4*page + 2]
        val allUserData4 = allUserDataList[4*page + 3]
        val allUserWorldDataList1: List<String> = allUserData1.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
        val allUserWorldDataList2: List<String> = allUserData2.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
        val allUserWorldDataList3: List<String> = allUserData3.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
        val allUserWorldDataList4: List<String> = allUserData4.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거

        reduce {
            state.copy(
                userDataList = userDataList,
                patDataList = patDataList,
                itemDataList = itemDataList,
                allUserDataList =  allUserDataList,
                page = page,
                allUserData1 = allUserData1,
                allUserData2 = allUserData2,
                allUserData3 = allUserData3,
                allUserData4 = allUserData4,
                allUserWorldDataList1 = allUserWorldDataList1,
                allUserWorldDataList2 = allUserWorldDataList2,
                allUserWorldDataList3 = allUserWorldDataList3,
                allUserWorldDataList4 = allUserWorldDataList4,
                allUserRankDataList = allUserRankDataList
            )
        }
    }

    private fun loadChatMessages() {
        val todayDocId = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        Firebase.firestore.collection("chat")
            .document(todayDocId)
            .addSnapshotListener { snapshot, error ->
                Log.d("CommunityViewModel", "채팅 스냅샷 수신됨")

                if (error != null) {
                    Log.e("CommunityViewModel", "채팅 데이터 에러: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val messages = snapshot.data?.mapNotNull { (key, value) ->
                        val timestamp = key.toLongOrNull()
                        if (timestamp == null) {
                            Log.e("CommunityViewModel", "timestamp 변환 실패: $key")
                            return@mapNotNull null
                        }

                        val map = value as? Map<*, *>
                        val message = map?.get("message") as? String
                        val name = map?.get("name") as? String
                        val tag = map?.get("tag") as? String
                        val ban = map?.get("ban") as? String

                        if (message != null && name != null && tag != null && ban != null && ban != "1") {
                            ChatMessage(timestamp, message, name, tag, ban)
                        } else {
                            null
                        }
                    }?.sortedBy { it.timestamp } ?: emptyList()

                    viewModelScope.launch {
                        intent {
                            reduce {
                                state.copy(chatMessages = messages)
                            }
                        }
                    }
                } else {
                    Log.w("CommunityViewModel", "스냅샷은 존재하지 않음")
                }
            }
    }

    fun opPageUpClick() = intent {

        val page = state.page
        val allUserDataList = state.allUserDataList

        if (allUserDataList.size > page * 4 + 8) {
            //다음 페이지
            val allUserData1 = allUserDataList[4*page + 4]
            val allUserData2 = allUserDataList[4*page + 5]
            val allUserData3 = allUserDataList[4*page + 6]
            val allUserData4 = allUserDataList[4*page + 7]
            val allUserWorldDataList1: List<String> = allUserData1.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList2: List<String> = allUserData2.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList3: List<String> = allUserData3.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList4: List<String> = allUserData4.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거

            userDao.update(id = "etc", value = (page+1).toString())
            reduce {
                state.copy(
                    page = page + 1,
                    allUserData1 = allUserData1,
                    allUserData2 = allUserData2,
                    allUserData3 = allUserData3,
                    allUserData4 = allUserData4,
                    allUserWorldDataList1 = allUserWorldDataList1,
                    allUserWorldDataList2 = allUserWorldDataList2,
                    allUserWorldDataList3 = allUserWorldDataList3,
                    allUserWorldDataList4 = allUserWorldDataList4
                )
            }

        } else {
            //첫 페이지

            val allUserData1 = allUserDataList[4*page]
            val allUserData2 = allUserDataList[4*page + 1]
            val allUserData3 = allUserDataList[4*page + 2]
            val allUserData4 = allUserDataList[4*page + 3]
            val allUserWorldDataList1: List<String> = allUserData1.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList2: List<String> = allUserData2.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList3: List<String> = allUserData3.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList4: List<String> = allUserData4.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거

            userDao.update(id = "etc", value = "0")
            reduce {
                state.copy(
                    page = 0,
                    allUserData1 = allUserData1,
                    allUserData2 = allUserData2,
                    allUserData3 = allUserData3,
                    allUserData4 = allUserData4,
                    allUserWorldDataList1 = allUserWorldDataList1,
                    allUserWorldDataList2 = allUserWorldDataList2,
                    allUserWorldDataList3 = allUserWorldDataList3,
                    allUserWorldDataList4 = allUserWorldDataList4
                )
            }
        }

    }

    fun onSituationChange(newSituation: String) = intent {
        reduce {
            val sortedList = when (newSituation) {
                "firstGame" -> state.allUserRankDataList.sortedByDescending { it.firstGame.toInt() }
                "secondGame" -> state.allUserRankDataList.sortedBy { it.secondGame.toInt() }
                "thirdGameEasy" -> state.allUserRankDataList.sortedByDescending { it.thirdGameEasy.toInt() }
                "thirdGameNormal" -> state.allUserRankDataList.sortedByDescending { it.thirdGameNormal.toInt() }
                "thirdGameHard" -> state.allUserRankDataList.sortedByDescending { it.thirdGameHard.toInt() }

                else -> state.allUserRankDataList
            }

            state.copy(
                situation = newSituation,
                allUserRankDataList = sortedList
            )
        }
    }

    fun onUserWorldClick(clickUserNumber: Int) = intent {
        val selectedUser = when (clickUserNumber) {
            1 -> state.allUserData1
            2 -> state.allUserData2
            3 -> state.allUserData3
            4 -> state.allUserData4
            else -> AllUser()
        }
        val selectedUserWorldDataList = when (clickUserNumber) {
            1 -> state.allUserWorldDataList1
            2 -> state.allUserWorldDataList2
            3 -> state.allUserWorldDataList3
            4 -> state.allUserWorldDataList4
            else -> emptyList()
        }
        reduce {
            state.copy(
                clickAllUserData = selectedUser,
                clickAllUserWorldDataList = selectedUserWorldDataList)
        }
    }

    fun onUserRankClick(userIndex: Int) = intent {
        val selectedUser = state.allUserDataList.get(index = userIndex)
        val selectedUserWorldDataList: List<String> = selectedUser.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거

        reduce {
            state.copy(
                clickAllUserData = selectedUser,
                clickAllUserWorldDataList = selectedUserWorldDataList)
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

        if (chatText.length <= 50) {
            reduce {
                state.copy(newChat = chatText)
            }
        }
    }

    fun onLikeClick() = intent {

        val db = Firebase.firestore
        val myUid = state.userDataList.find { it.id == "auth" }!!.value
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) // "20250516"
        val docRef = db.collection("users").document(myUid).collection("community").document(today)
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
                                                Log.d("TAG", "like 값이 $likeValue → $newLikeValue 으로 업데이트됨")
                                                viewModelScope.launch {
                                                    allUserDao.updateLikeByTag(tag = tag, newLike = newLikeValue.toString())
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
                                        allUserDao.updateLikeByTag(tag = tag, newLike = (state.clickAllUserData.like.toInt() + 1).toString() )
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
                            postSideEffect(CommunitySideEffect.Toast("좋아요를 눌렀습니다."))
                        }
                    } else {
                        // 이미 존재할 때 Toast 띄우기
                        viewModelScope.launch {
                            postSideEffect(CommunitySideEffect.Toast("이미 좋아요를 눌렀습니다."))
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
                                            Log.d("TAG", "like 값이 $likeValue → $newLikeValue 으로 업데이트됨")
                                            viewModelScope.launch {
                                                allUserDao.updateLikeByTag(tag = tag, newLike = newLikeValue.toString())
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
                                    allUserDao.updateLikeByTag(tag = tag, newLike = (state.clickAllUserData.like.toInt() + 1).toString() )
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
                        postSideEffect(CommunitySideEffect.Toast("좋아요를 눌렀습니다."))
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error accessing community document", e)
                viewModelScope.launch {
                    postSideEffect(CommunitySideEffect.Toast("인터넷 오류."))
                }
            }

        loadData()

    }

}

@Immutable
data class CommunityState(
    val userDataList: List<User> = emptyList(),
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val page: Int = 0,
    val allUserDataList: List<AllUser> = emptyList(),
    val allUserData1: AllUser = AllUser(),
    val allUserData2: AllUser = AllUser(),
    val allUserData3: AllUser = AllUser(),
    val allUserData4: AllUser = AllUser(),
    val allUserWorldDataList1: List<String> = emptyList(),
    val allUserWorldDataList2: List<String> = emptyList(),
    val allUserWorldDataList3: List<String> = emptyList(),
    val allUserWorldDataList4: List<String> = emptyList(),
    val situation: String = "world",
    val clickAllUserData: AllUser = AllUser(),
    val clickAllUserWorldDataList: List<String> = emptyList(),
    val allUserRankDataList: List<AllUser> = emptyList(),
    val newChat: String = "",
    val chatMessages: List<ChatMessage> = emptyList()
)

@Immutable
data class ChatMessage(
    val timestamp: Long,
    val message: String,
    val name: String,
    val tag: String,
    val ban: String
)


//상태와 관련없는 것
sealed interface CommunitySideEffect{
    class Toast(val message:String): CommunitySideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}