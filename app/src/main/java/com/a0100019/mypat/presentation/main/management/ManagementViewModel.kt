package com.a0100019.mypat.presentation.main.management

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.knowledge.KnowledgeDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.data.room.letter.LetterDao
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.sudoku.SudokuDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.data.room.world.WorldDao
import com.a0100019.mypat.presentation.login.LoginSideEffect
import com.a0100019.mypat.presentation.activity.store.StoreSideEffect
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val userDao: UserDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val diaryDao: DiaryDao,
    private val englishDao: EnglishDao,
    private val koreanIdiomDao: KoreanIdiomDao,
    private val sudokuDao: SudokuDao,
    private val walkDao: WalkDao,
    private val worldDao: WorldDao,
    private val letterDao: LetterDao,
    private val areaDao: AreaDao,
    private val allUserDao: AllUserDao,
    private val knowledgeDao: KnowledgeDao,
    @ApplicationContext private val context: Context

) : ViewModel(), ContainerHost<ManagementState, ManagementSideEffect> {

    override val container: Container<ManagementState, ManagementSideEffect> = container(
        initialState = ManagementState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(ManagementSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    init {
        newLetterGet()
        onCommunityLoad()
        dataSave()
    }


    private fun newLetterGet() = intent {

        val letterDocRef = Firebase.firestore
            .collection("code")
            .document("letter")

        val tag = userDao.getValue2ById("auth")

        try {
            // 🔹 Firestore 문서 가져오기 (대기)
            val snapshot = letterDocRef.get().await()
            if (!snapshot.exists()) return@intent

            val letterMap =
                snapshot.data as? Map<String, Map<String, String>>
                    ?: return@intent

            // 🔹 모든 편지 순차 처리
            letterMap.forEach { (key, value) ->

                val baseId = key.toIntOrNull() ?: return@forEach
                val isPersonalLetter = key.startsWith("90")
                var shouldDelete = false

                val shouldInsert = when {
                    isPersonalLetter -> {
                        val subId = key.drop(2)
                        val match = (tag == subId)
                        if (match) shouldDelete = true
                        match
                    }
                    else -> true
                }

                if (!shouldInsert) return@forEach

                // ✅ Room id 계산 (순차라 안전)
                val finalId = if (isPersonalLetter) {
                    val maxId = letterDao.getMaxIdStartingFrom(baseId)
                    (maxId ?: (baseId - 1)) + 1
                } else {
                    baseId
                }

                val letter = Letter(
                    id = finalId,
                    amount = value["amount"].orEmpty(),
                    date = value["date"].orEmpty(),
                    link = value["link"].orEmpty(),
                    message = value["message"].orEmpty(),
                    reward = value["reward"].orEmpty(),
                    state = value["state"].orEmpty(),
                    title = value["title"].orEmpty()
                )

                letterDao.insertIgnore(letter)

                // 🔥 개인 편지는 Firestore에서 삭제 (대기)
                if (shouldDelete) {
                    letterDocRef.update(key, FieldValue.delete()).await()
                }
            }

            Log.e("Firestore", "letter 확인")

        } catch (e: Exception) {
            Log.e("Firestore", "letter 처리 실패", e)

        }
    }


    private fun onCommunityLoad() = intent {

        viewModelScope.launch {
            try {
                // 🔑 현재 로그인한 유저 uid
                val uid = userDao.getValueById("auth")
                if (uid.isEmpty()) return@launch

                val userDocRef = Firebase.firestore
                    .collection("users")
                    .document(uid)

                val snapshot = userDocRef.get().await()

                // community map
                val communityMap = snapshot.get("community") as? Map<String, Any>

                // ✅ like 값
                val likeValue = communityMap?.get("like") as? String
                if (likeValue != null) {
                    userDao.update(
                        id = "community",
                        value = likeValue
                    )
                    Log.d("Firestore", "community.like 업데이트: $likeValue")
                }

                // ✅ ban 값 → value3에 저장
                val banValue = communityMap?.get("ban") as? String
                if (banValue != null) {
                    userDao.update(
                        id = "community",
                        value3 = banValue
                    )
                    Log.d("Firestore", "community.ban 업데이트: $banValue")
                }

            } catch (e: Exception) {
                Log.e("Firestore", "community 데이터 가져오기 실패", e)
            }
        }
    }


    private fun dataSave() = intent {

        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        //data 파이어베이스에 저장
        try {

            // ... 전체 dataSave() 내용
            val db = Firebase.firestore
            val userDataList = userDao.getAllUserData()
            val userId = userDataList.find { it.id == "auth" }!!.value
            val itemDataList = itemDao.getAllItemDataWithShadow()
            val patDataList = patDao.getAllPatData()
            val worldDataList = worldDao.getAllWorldData()
            val letterDataList = letterDao.getAllLetterData()
            val walkDataList = walkDao.getAllWalkData()
            val englishDataList = englishDao.getOpenEnglishData()
            val koreanIdiomDataList = koreanIdiomDao.getOpenKoreanIdiomData()
            val diaryDataList = diaryDao.getAllDiaryData()
            val sudokuDataList = sudokuDao.getAllSudokuData()
            val areaDataList = areaDao.getAllAreaData()
            val knowledgeList = knowledgeDao.getAllKnowledgeData()

            val batch = db.batch()

            val userData = mapOf(
                "cash" to userDataList.find { it.id == "money"}!!.value2,
                "money" to userDataList.find { it.id == "money"}!!.value,
                "stepsRaw" to userDataList.find { it.id == "etc2" }!!.value2,
                "pay" to userDataList.find { it.id == "name"}!!.value3,

                "community" to mapOf(
                    "ban" to userDataList.find { it.id == "community"}!!.value3,
//                "like" to userDataList.find { it.id == "community"}!!.value,
                    "warning" to userDataList.find {it.id == "community"}!!.value2,
                    "medal" to userDataList.find { it.id == "etc"}!!.value3,
                    "medalQuest" to userDataList.find { it.id == "name"}!!.value2,
                    "introduction" to userDataList.find { it.id == "etc"}!!.value,
                    "medalCount" to userDataList.find { it.id == "etc"}!!.value3.count { it == '/' },
                ),

                "date" to mapOf(
                    "firstDate" to userDataList.find { it.id == "date"}!!.value3,
                    "totalDate" to userDataList.find { it.id == "date"}!!.value2,
                    "lastDate" to userDataList.find { it.id == "date"}!!.value
                ),

                "game" to mapOf(
                    "firstGame" to userDataList.find { it.id == "firstGame"}!!.value,
                    "secondGame" to userDataList.find { it.id == "secondGame"}!!.value,
                    "thirdGameEasy" to userDataList.find { it.id == "thirdGame"}!!.value,
                    "thirdGameNormal" to userDataList.find { it.id == "thirdGame"}!!.value2,
                    "thirdGameHard" to userDataList.find { it.id == "thirdGame"}!!.value3,
                ),

                "item" to mapOf(
                    "openItem" to itemDataList.count { it.date != "0"}.toString(),
                    "openItemSpace" to userDataList.find { it.id == "item"}!!.value2,
                    "useItem" to userDataList.find { it.id == "item"}!!.value3
                ),

                "pat" to mapOf(
                    "openPat" to patDataList.count { it.date != "0"}.toString(),
                    "openPatSpace" to userDataList.find { it.id == "pat"}!!.value2,
                    "usePat" to userDataList.find { it.id == "pat"}!!.value3
                ),

                "area" to worldDataList.find { it.id == 1}!!.value,
                "name" to userDataList.find { it.id == "name"}!!.value,
                "lastLogin" to userDataList.find { it.id == "auth"}!!.value3,
                "tag" to userDataList.find { it.id == "auth"}!!.value2,
                "openArea" to areaDataList.count { it.date != "0"}.toString(),

                "online" to "0",

                "walk" to mapOf(
                    "saveWalk" to userDataList.find { it.id == "walk"}!!.value,
                    "totalWalk" to userDataList.find { it.id == "walk"}!!.value3,
                )

            )

            // 🔹 월드 데이터 만들기
            val worldMap = worldDataList.drop(1)
                .mapIndexed { index, data ->
                    if (data.type == "pat") {
                        val patData = patDataList.find { it.id == data.value.toInt() }
                        index.toString() to mapOf(
                            "id" to data.value,
                            "size" to patData!!.sizeFloat.toString(),
                            "type" to data.type,
                            "x" to patData.x.toString(),
                            "y" to patData.y.toString(),
                            "effect" to patData.effect.toString()
                        )
                    } else {
                        val itemData = itemDataList.find { it.id == data.value.toInt() }
                        index.toString() to mapOf(
                            "id" to data.value,
                            "size" to itemData!!.sizeFloat.toString(),
                            "type" to data.type,
                            "x" to itemData.x.toString(),
                            "y" to itemData.y.toString(),
                            "effect" to "0"
                        )
                    }
                }
                .toMap()

            val userDocRef = Firebase.firestore.collection("users").document(userId)

            // 1) 문서 보장 (없으면 생성)
            batch.set(userDocRef, emptyMap<String, Any>(), SetOptions.merge())

            // 2) 기존 world 필드 제거
            batch.update(userDocRef, mapOf("world" to FieldValue.delete()))

            // 3) userData + 새 world 필드 병합 저장
            val finalData = userData + mapOf("world" to worldMap)
            batch.set(userDocRef, finalData, SetOptions.merge())

            //펫 데이터 저장
            val patCollectionRef = db.collection("users")
                .document(userId)
                .collection("dataCollection")

            val combinedPatData = mutableMapOf<String, Any>()
            patDataList
                .filter { it.date != "0" }
                .forEach { patData ->
                    val patMap = mapOf(
                        "date" to patData.date,
                        "love" to patData.love.toString(),
                        "size" to patData.sizeFloat.toString(),
                        "x" to patData.x.toString(),
                        "y" to patData.y.toString(),
                        "gameCount" to patData.gameCount.toString(),
                        "effect" to patData.effect.toString()
                    )
                    combinedPatData[patData.id.toString()] = patMap
                }
            batch.set(patCollectionRef.document("pats"), combinedPatData)

            val itemCollectionRef = db.collection("users")
                .document(userId)
                .collection("dataCollection")

            val combinedItemData = mutableMapOf<String, Any>()
            itemDataList
                .filter { it.date != "0" }
                .forEach { itemData ->
                    val itemMap = mapOf(
                        "date" to itemData.date,
                        "size" to itemData.sizeFloat.toString(),
                        "x" to itemData.x.toString(),
                        "y" to itemData.y.toString()
                    )
                    combinedItemData[itemData.id.toString()] = itemMap
                }
            batch.set(itemCollectionRef.document("items"), combinedItemData)

            val areaCollectionRef = db.collection("users")
                .document(userId)
                .collection("dataCollection")

            val combinedMapData = mutableMapOf<String, Any>()
            areaDataList
                .filter { it.date != "0" }
                .forEach { areaData ->
                    val areaMap = mapOf(
                        "date" to areaData.date,
                    )
                    combinedMapData[areaData.id.toString()] = areaMap
                }
            batch.set(areaCollectionRef.document("areas"), combinedMapData)

            val letterCollectionRef = db.collection("users")
                .document(userId)
                .collection("dataCollection")

            val combinedLetterData = mutableMapOf<String, Any>()
            letterDataList.forEach { letterData ->
                val letterMap = mapOf(
                    "date" to letterData.date,
                    "title" to letterData.title,
                    "message" to letterData.message,
                    "link" to letterData.link,
                    "reward" to letterData.reward,
                    "amount" to letterData.amount,
                    "state" to letterData.state,
                )
                combinedLetterData[letterData.id.toString()] = letterMap
            }
            // 하나의 문서에 전체 데이터를 저장
            batch.set(letterCollectionRef.document("letters"), combinedLetterData)

            val sudokuCollectionRef = db.collection("users")
                .document(userId)
                .collection("dataCollection")
                .document("sudoku")

            val sudokuData = mapOf(
                "sudokuBoard" to sudokuDataList.find {it.id == "sudokuBoard"}!!.value,
                "sudokuFirstBoard" to sudokuDataList.find {it.id == "sudokuFirstBoard"}!!.value,
                "sudokuMemoBoard" to sudokuDataList.find {it.id == "sudokuMemoBoard"}!!.value,
                "time" to sudokuDataList.find {it.id == "time"}!!.value,
                "level" to sudokuDataList.find {it.id == "level"}!!.value,
                "state" to sudokuDataList.find {it.id == "state"}!!.value
            )
            batch.set(sudokuCollectionRef, sudokuData)

            val dailyCollectionRef = db.collection("users")
                .document(userId)
                .collection("daily")

            diaryDataList.forEach { diary ->
                val docRef = dailyCollectionRef.document(diary.id.toString())

                val date = diary.date

                val walk = walkDataList.find { it.id == diary.id }?.success

                // state 구성 (둘 중 하나라도 null이면 제외)
                val englishState = englishDataList.find { it.id == diary.id }?.state
                val idiomState = koreanIdiomDataList.find { it.id == diary.id }?.state

                val data = mutableMapOf<String, Any>(
                    "date" to diary.date,
                    "diary" to mapOf(
                        "emotion" to diary.emotion,
                        "state" to diary.state,
                        "contents" to diary.contents
                    )
                )

                if(walk != null) {
                    data["walk"] = walk
                }

                if (englishState != null && idiomState != null) {
                    data["state"] = mapOf(
                        "english" to englishState,
                        "koreanIdiom" to idiomState
                    )
                }

                val knowledgeState = knowledgeList.find {it.date == date}?.state
                if(knowledgeState != null) {
                    data["knowledge"] = knowledgeState
                }

                batch.set(docRef, data)
            }

            Log.d("Firestore", "batch.commit() 직전")

            // 전체 커밋 실행
            batch.commit()
                .addOnSuccessListener {
                    Log.e("Firestore", "일일 저장 성공")
                }
                .addOnFailureListener {
                    Log.e("Firestore", "저장 실패", it)
                }

        } catch (e: Exception) {
            Log.e("Firestore", "예외 발생", e)
        }

    }

}

@Immutable
data class ManagementState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList()
)

//상태와 관련없는 것
sealed interface ManagementSideEffect{
    class Toast(val message:String): ManagementSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}