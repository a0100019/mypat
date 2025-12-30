package com.a0100019.mypat.presentation.neighbor.community

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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao
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
        onFirstGameRank()
        onSecondGameRank()
        onThirdGameEasyRank()
        onThirdGameNormalRank()
        onThirdGameHardRank()
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

        val page = 0
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
                page = page,
                allUserData1 = allUserData1,
                allUserData2 = allUserData2,
                allUserData3 = allUserData3,
                allUserData4 = allUserData4,
                allUserWorldDataList1 = allUserWorldDataList1,
                allUserWorldDataList2 = allUserWorldDataList2,
                allUserWorldDataList3 = allUserWorldDataList3,
                allUserWorldDataList4 = allUserWorldDataList4,
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

    fun onUpdateCheckClick() = intent {

        reduce {
            state.copy(
                situation = "updateLoading"
            )
        }

        val db = Firebase.firestore
        db.collection("users")
            .orderBy("lastLogin", Query.Direction.DESCENDING) // 최신순 정렬
            .limit(1000) // 최대 1000개만 가져오기
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    try {
                        val gameMap = doc.get("game") as? Map<String, String> ?: emptyMap()
                        val communityMap = doc.get("community") as? Map<String, String> ?: emptyMap()
                        val dateMap = doc.get("date") as? Map<String, String> ?: emptyMap()
                        val itemMap = doc.get("item") as? Map<String, String> ?: emptyMap()
                        val patMap = doc.get("pat") as? Map<String, String> ?: emptyMap()

                        val worldMap = doc.get("world") as? Map<String, Map<String, String>> ?: emptyMap()

                        val worldData = worldMap.entries.joinToString("/") { (_, innerMap) ->
                            val id = innerMap["id"].orEmpty()
                            val size = innerMap["size"].orEmpty()
                            val type = innerMap["type"].orEmpty()
                            val x = innerMap["x"].orEmpty()
                            val y = innerMap["y"].orEmpty()
                            val effect = innerMap["effect"].orEmpty()
                            "$id@$size@$type@$x@$y@$effect"
                        }

                        val allUser = AllUser(
                            tag = doc.getString("tag").orEmpty(),
                            lastLogin = doc.getString("lastLogin").orEmpty().toLongOrNull() ?: 0L,
                            ban = communityMap["ban"].orEmpty(),
                            like = communityMap["like"].orEmpty(),
                            warning = communityMap["introduction"].orEmpty() + "@" + communityMap["medal"].orEmpty(),
                            firstDate = dateMap["firstDate"].orEmpty(),
                            firstGame = gameMap["firstGame"].orEmpty(),
                            secondGame = gameMap["secondGame"].orEmpty(),
                            thirdGameEasy = gameMap["thirdGameEasy"].orEmpty(),
                            thirdGameNormal = gameMap["thirdGameNormal"].orEmpty(),
                            thirdGameHard = gameMap["thirdGameHard"].orEmpty(),
                            openItem = itemMap["openItem"].orEmpty(),
                            area = doc.getString("area").orEmpty(),
                            name = doc.getString("name").orEmpty(),
                            openPat = patMap["openPat"].orEmpty(),
                            openArea = doc.getString("openArea").orEmpty(),
                            totalDate = dateMap["totalDate"].orEmpty(),
                            worldData = worldData
                        )

                        viewModelScope.launch {
                            allUserDao.insert(allUser)
                        }

                    } catch (e: Exception) {
                        Log.e("DB", "문서 처리 실패: ${doc.id}", e)
                    }
                }

                viewModelScope.launch {
                    val uid = userDao.getValueById("auth")

                    val userDocRef = Firebase.firestore
                        .collection("users")
                        .document(uid)

                    try {
                        val snapshot = userDocRef.get().await()

                        // community map 가져오기
                        val communityMap = snapshot.get("community") as? Map<String, Any>

                        // like 값
                        val likeValue = communityMap?.get("like") as? String
                        if (likeValue != null) {
                            userDao.update(id = "community", value = likeValue)
                            Log.d("Firestore", "community.like = $likeValue 로 업데이트 완료")
                        } else {
                            Log.d("Firestore", "community.like 없음 → 업데이트 취소")
                        }

                        // 🔥 ban 값 → value3에 저장
                        val banValue = communityMap?.get("ban") as? String
                        if (banValue != null) {
                            userDao.update(id = "community", value3 = banValue)
                            Log.d("Firestore", "community.ban = $banValue 로 value3 업데이트 완료")
                        } else {
                            Log.d("Firestore", "community.ban 없음 → 업데이트 취소")
                        }

                    } catch (e: Exception) {
                        Log.e("Firestore", "community 데이터 가져오기 실패", e)
                    }
                }

                viewModelScope.launch {
                    try {
                        userDao.update(
                            id = "etc",
                            value2 = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )

                        reduce { state.copy(situation = "world") }

                        loadData()

                    } catch (e: Exception) {
                        Log.e("DB", "update 실패", e)
                    }
                }

                Log.e("login", "allUser 가져옴")
            }
            .addOnFailureListener { e ->
                Log.e("login", "users 컬렉션 가져오기 실패", e)
                viewModelScope.launch {
                    postSideEffect(CommunitySideEffect.Toast("인터넷 연결 오류"))
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

            val allUserData1 = allUserDataList[0]
            val allUserData2 = allUserDataList[1]
            val allUserData3 = allUserDataList[2]
            val allUserData4 = allUserDataList[3]
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
            state.copy(
                situation = newSituation
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
        userDao.update(id = "etc2", value3 = selectedUser.tag)
        postSideEffect(CommunitySideEffect.NavigateToNeighborInformationScreen)
    }

    fun onNeighborInformationClick(neighborTag: String) = intent {

        userDao.update(id = "etc2", value3 = neighborTag)
        postSideEffect(CommunitySideEffect.NavigateToNeighborInformationScreen)

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

    private fun onFirstGameRank() = intent {

        try {
            val docSnap = Firebase.firestore
                .collection("rank")
                .document("firstGame")
                .get()
                .await()

            if (!docSnap.exists()) return@intent

            val rankList = docSnap.data
                ?.toList() // Map → List<Pair<String, Any>>
                ?.sortedBy { (key, _) ->
                    // "1", "10", "100" → 숫자 정렬
                    key.toIntOrNull() ?: Int.MAX_VALUE
                }
                ?.mapNotNull { (_, value) ->
                    val map = value as? Map<*, *> ?: return@mapNotNull null

                    Rank(
                        name = map["name"] as? String ?: "",
                        tag = map["tag"] as? String ?: "",
                        ban = map["ban"] as? String ?: "0",
                        score = map["score"] as? String ?: "0",
                    )
                }
                ?: emptyList()

            reduce {
                state.copy(
                    firstGameRankList = rankList
                )
            }

        } catch (e: Exception) {
            Log.e("Rank", "firstGame 랭킹 로드 실패", e)
        }
    }


    private fun onSecondGameRank() = intent {

        try {
            val docSnap = Firebase.firestore
                .collection("rank")
                .document("secondGame")
                .get()
                .await()

            if (!docSnap.exists()) return@intent

            val rankList = docSnap.data
                ?.toList() // Map → List<Pair<String, Any>>
                ?.sortedBy { (key, _) ->
                    // "1", "10", "100" → 숫자 기준 정렬
                    key.toIntOrNull() ?: Int.MAX_VALUE
                }
                ?.mapNotNull { (_, value) ->
                    val map = value as? Map<*, *> ?: return@mapNotNull null

                    Rank(
                        name = map["name"] as? String ?: "",
                        tag = map["tag"] as? String ?: "",
                        ban = map["ban"] as? String ?: "0",
                        score = map["score"] as? String ?: "0", // "39.829"
                    )
                }
                ?: emptyList()

            reduce {
                state.copy(
                    secondGameRankList = rankList
                )
            }

        } catch (e: Exception) {
            Log.e("Rank", "secondGame 랭킹 로드 실패", e)
        }
    }


    private fun onThirdGameEasyRank() = intent {

        try {
            val docSnap = Firebase.firestore
                .collection("rank")
                .document("thirdGameEasy")
                .get()
                .await()

            if (!docSnap.exists()) return@intent

            val rankList = docSnap.data
                ?.toList()
                ?.sortedBy { (key, _) ->
                    key.toIntOrNull() ?: Int.MAX_VALUE
                }
                ?.mapNotNull { (_, value) ->
                    val map = value as? Map<*, *> ?: return@mapNotNull null

                    Rank(
                        name = map["name"] as? String ?: "",
                        tag = map["tag"] as? String ?: "",
                        ban = map["ban"] as? String ?: "0",
                        score = map["score"] as? String ?: "0",
                    )
                }
                ?: emptyList()

            reduce {
                state.copy(
                    thirdGameEasyRankList = rankList
                )
            }

        } catch (e: Exception) {
            Log.e("Rank", "thirdGameEasy 랭킹 로드 실패", e)
        }
    }


    private fun onThirdGameNormalRank() = intent {

        try {
            val docSnap = Firebase.firestore
                .collection("rank")
                .document("thirdGameNormal")
                .get()
                .await()

            if (!docSnap.exists()) return@intent

            val rankList = docSnap.data
                ?.toList()
                ?.sortedBy { (key, _) ->
                    key.toIntOrNull() ?: Int.MAX_VALUE
                }
                ?.mapNotNull { (_, value) ->
                    val map = value as? Map<*, *> ?: return@mapNotNull null

                    Rank(
                        name = map["name"] as? String ?: "",
                        tag = map["tag"] as? String ?: "",
                        ban = map["ban"] as? String ?: "0",
                        score = map["score"] as? String ?: "0",
                    )
                }
                ?: emptyList()

            reduce {
                state.copy(
                    thirdGameNormalRankList = rankList
                )
            }

        } catch (e: Exception) {
            Log.e("Rank", "thirdGameNormal 랭킹 로드 실패", e)
        }
    }

    private fun onThirdGameHardRank() = intent {

        try {
            val docSnap = Firebase.firestore
                .collection("rank")
                .document("thirdGameHard")
                .get()
                .await()

            if (!docSnap.exists()) return@intent

            val rankList = docSnap.data
                ?.toList()
                ?.sortedBy { (key, _) ->
                    key.toIntOrNull() ?: Int.MAX_VALUE
                }
                ?.mapNotNull { (_, value) ->
                    val map = value as? Map<*, *> ?: return@mapNotNull null

                    Rank(
                        name = map["name"] as? String ?: "",
                        tag = map["tag"] as? String ?: "",
                        ban = map["ban"] as? String ?: "0",
                        score = map["score"] as? String ?: "0",
                    )
                }
                ?: emptyList()

            reduce {
                state.copy(
                    thirdGameHardRankList = rankList
                )
            }

        } catch (e: Exception) {
            Log.e("Rank", "thirdGameHard 랭킹 로드 실패", e)
        }
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
    val newChat: String = "",
    val chatMessages: List<ChatMessage> = emptyList(),
    val allAreaCount: String = "",
    val text2: String = "",
    val text3: String = "",
    val dialogState: String = "",
    val firstGameRankList: List<Rank> = emptyList(),
    val secondGameRankList: List<Rank> = emptyList(),
    val thirdGameEasyRankList: List<Rank> = emptyList(),
    val thirdGameNormalRankList: List<Rank> = emptyList(),
    val thirdGameHardRankList: List<Rank> = emptyList(),

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

@Immutable
data class Rank(
    val ban: String = "0",
    val name: String = "이웃",
    val tag: String = "0",
    val score: String = "0",
)

//상태와 관련없는 것
sealed interface CommunitySideEffect{
    class Toast(val message:String): CommunitySideEffect
    data object NavigateToNeighborInformationScreen: CommunitySideEffect

}