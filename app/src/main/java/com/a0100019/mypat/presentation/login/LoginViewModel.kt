package com.a0100019.mypat.presentation.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.data.room.letter.LetterDao
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.knowledge.KnowledgeDao
import com.a0100019.mypat.data.room.knowledge.getKnowledgeInitialData
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.sudoku.SudokuDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.data.room.world.WorldDao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
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
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container: Container<LoginState, LoginSideEffect> = container(
        initialState = LoginState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(LoginSideEffect.Toast(message = throwable.message.orEmpty()))
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
        val loginState = userDataList.find { it.id == "auth" }!!.value

        if (knowledgeDao.count() == 0) {
            knowledgeDao.insertAll(getKnowledgeInitialData())
        }

        if(loginState == "0") {
            reduce {
                state.copy(
                    loginState = "unLogin"
                )
            }
        } else {
            reduce {
                state.copy(
                    loginState = "loading"
                )
            }

            dataSave()
            newLetterGet()
            onCommunityLoad()

        }

    }

    fun reLoading() = intent {
        loadData()
    }

    fun onGoogleLoginClick(idToken: String) = intent {
        Log.e("login", "idToken = $idToken")

        if (state.isLoggingIn) return@intent

        reduce { state.copy(isLoggingIn = true) }

        try {

            //auth에 계정 생성
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            val user = authResult.user
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

            Log.e("login", "user = $user, isNewUser = $isNewUser")

            user?.let {
                if (isNewUser) {

                    // 🔹 신규 사용자일 때만 실행되는 코드
                    val db = FirebaseFirestore.getInstance()

                    //tag 설정
                    val lastKey: Int = withContext(Dispatchers.IO) {
                        val documentSnapshot = db.collection("tag")
                            .document("tag")
                            .get()
                            .await()

                        val dataMap = documentSnapshot.data ?: emptyMap()

                        dataMap.keys.maxOfOrNull { it.toInt() }!!
                    }
                    userDao.update(id = "auth", value = user.uid, value2 = "${lastKey+1}")

                    val firestore = Firebase.firestore
                    val tagDocRef = firestore.collection("tag").document("tag")
                    tagDocRef.get().addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val data = document.data.orEmpty()

                            // 키가 숫자인 필드들 중 가장 큰 숫자 찾기
                            val maxKey = data.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: -1
                            val nextKey = (maxKey + 1).toString()

                            // 새로운 필드 추가
                            val newField = mapOf(nextKey to user.uid)

                            // 문서 업데이트
                            tagDocRef.update(newField)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Field 추가 성공: $nextKey -> hello")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Field 추가 실패", e)
                                }
                        } else {
                            Log.e("Firestore", "문서가 존재하지 않음")
                        }
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "문서 읽기 실패", e)
                    }

                    val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    userDao.update(id = "date", value3 = currentDate)

                    letterDao.updateDateByTitle(title = "시작의 편지", todayDate = currentDate)
                    val userRef = db.collection("users").document(it.uid)
                    userRef.set(
                        mapOf(
                            "online" to "1",
                            "community" to mapOf(
                                "like" to "0"
                            )
                        ),
                        SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            Log.d("login", "online=1, community.like=0 저장 성공")
                        }
                        .addOnFailureListener { e ->
                            Log.e("login", "저장 실패", e)
                        }

                    dataSave()

                    Log.e("login", "신규 사용자입니다")
//                    postSideEffect(LoginSideEffect.Toast("환영합니다!"))

                    reduce {
                        state.copy(
                            dialog = "explanation"
                        )
                    }

                } else {

                    // 🔹 기존 사용자일 경우 처리
                    Log.e("login", "기존 사용자입니다")

                    // Firestore에서 유저 데이터 가져오기
                    val db = FirebaseFirestore.getInstance()
                    try {
                        val userDoc = db.collection("users").document(it.uid).get().await()
                        if (userDoc.exists()) {

                            // 🔹 online 필드 확인
                            val online = userDoc.getString("online")
                            if (online == "1") {
                                if(state.dialog != "check"){
                                    Log.w("login", "이미 로그인 중인 사용자입니다")
                                    reduce {
                                        state.copy(
                                            dialog = "loginWarning"
                                        )
                                    }
                                    return@intent // 또는 return (코루틴/함수 구조에 따라)
                                }
                            } else {
                                // 🔹 online 필드가 0이면 1로 업데이트
                                db.collection("users").document(it.uid)
                                    .update("online", "1")
                                    .addOnSuccessListener {
                                        Log.d("login", "online 필드가 1로 업데이트됨")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("login", "online 필드 업데이트 실패", e)
                                    }
                            }

                            val money = userDoc.getString("money")
                            val cash = userDoc.getString("cash")
                            userDao.update(id = "money", value = money, value2 = cash)

                            val saveStepsRaw = userDoc.getString("stepsRaw")

                            // SharedPreferences 불러오기
                            val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
                            prefs.edit()
                                .putString("stepsRaw", saveStepsRaw)
                                .apply()

                            userDao.update(id = "etc2", value2 = saveStepsRaw)

                            val pay = userDoc.getString("pay")
                            userDao.update(id = "name", value3 = pay)

                            val communityMap = userDoc.get("community") as Map<String, String>
                            val ban = communityMap["ban"]
                            val like = communityMap["like"]
                            val warning = communityMap["warning"]
                            userDao.update(id = "community", value = like, value2 = warning, value3 = ban)

                            val medal = communityMap["medal"]
                            val introduction = communityMap["introduction"]
                            if (medal != null && introduction != null) {
                                userDao.update(id = "etc", value = introduction, value3 = medal)
                            }
                            val medalQuest = communityMap["medalQuest"]
                            if (medalQuest != null) {
                                userDao.update(id = "name", value2 = medalQuest)
                            }

                            val dateMap = userDoc.get("date") as Map<String, String>
                            val firstDate = dateMap["firstDate"]
                            val totalDate = dateMap["totalDate"]
                            val lastDate = dateMap["lastDate"]
                            userDao.update(id = "date", value = lastDate, value2 = totalDate, value3 = firstDate)

                            val gameMap = userDoc.get("game") as Map<String, String>
                            val firstGame = gameMap["firstGame"]
                            val secondGame = gameMap["secondGame"]
                            val thirdGameEasy = gameMap["thirdGameEasy"]
                            val thirdGameNormal = gameMap["thirdGameNormal"]
                            val thirdGameHard = gameMap["thirdGameHard"]
                            userDao.update(id = "firstGame", value = firstGame)
                            userDao.update(id = "secondGame", value = secondGame)
                            userDao.update(id = "thirdGame", value = thirdGameEasy, value2 = thirdGameNormal, value3 = thirdGameHard)

                            val itemMap = userDoc.get("item") as Map<String, String>
                            val openItemSpace = itemMap["openItemSpace"]
                            val useItem = itemMap["useItem"]
                            userDao.update(id = "item", value2 = openItemSpace, value3 = useItem)

                            val patMap = userDoc.get("pat") as Map<String, String>
                            val openPatSpace = patMap["openPatSpace"]
                            val usePat = patMap["usePat"]
                            userDao.update(id = "pat", value2 = openPatSpace, value3 = usePat)

                            val name = userDoc.getString("name")
                            userDao.update(id = "name", value = name)
                            val tag = userDoc.getString("tag")
                            userDao.update(id = "auth", value = it.uid, value2 = tag)

                            val walkMap = userDoc.get("walk") as Map<String, String>
                            val saveWalk = walkMap["saveWalk"]
                            val totalWalk = walkMap["totalWalk"]
                            userDao.update(id = "walk", value = saveWalk, value3 = totalWalk)

                            //오류 안나게 월드 데이터 한번 지움
                            worldDao.deleteAllWorlds()

                            val area = userDoc.getString("area")
                            worldDao.insert(World(id = 1, value = area.toString(), type = "area"))

                            val worldMap = userDoc.get("world") as Map<String, Map<String, String>>
                            for ((index, innerMap) in worldMap) {
                                val id = innerMap["id"]
//                                val size = innerMap["size"]
                                val type = innerMap["type"]
//                                val x = innerMap["x"]
//                                val y = innerMap["y"]

                                worldDao.insert(World(id = index.toInt()+2, value = id.toString(), type = type.toString()))
//                                Log.d("Firestore", "[$key] color=$color, font=$font")
                            }

                            //daily 서브컬렉션
                            val dailySubCollectionSnapshot = db
                                .collection("users")
                                .document(it.uid)
                                .collection("daily")
                                .get()
                                .await()

                            val dailyDocs = dailySubCollectionSnapshot.documents
                                .sortedBy { it.id.toIntOrNull() ?: Int.MAX_VALUE }

                            for (dailyDoc in dailyDocs) {
                                // 숫자 순서대로 처리됨
                                val date = dailyDoc.getString("date") ?: continue

                                val diaryMap = dailyDoc.get("diary") as? Map<*, *>
                                val diaryContents = diaryMap?.get("contents") as? String ?: ""
                                val diaryEmotion = diaryMap?.get("emotion") as? String ?: ""
                                val diaryState = diaryMap?.get("state") as? String ?: ""
                                diaryDao.insert(
                                    Diary(
                                        id = dailyDoc.id.toInt(),
                                        date = date,
                                        emotion = diaryEmotion,
                                        state = diaryState,
                                        contents = diaryContents
                                    )
                                )

                                // ✅ state 필드가 존재할 경우에만 처리
                                val stateMap = dailyDoc.get("state") as? Map<*, *>
                                val englishState = stateMap?.get("english") as? String
                                val koreanIdiomState = stateMap?.get("koreanIdiom") as? String

                                if (englishState != null) {
                                    englishDao.updateDateAndState(
                                        id = dailyDoc.id.toInt(),
                                        date = date,
                                        state = englishState
                                    )
                                }

                                if (koreanIdiomState != null) {
                                    koreanIdiomDao.updateDateAndState(
                                        id = dailyDoc.id.toInt(),
                                        date = date,
                                        state = koreanIdiomState
                                    )
                                }

                                val walk = dailyDoc.getString("walk")

                                if (!walk.isNullOrBlank()) {
                                    walkDao.insert(
                                        Walk(
                                            id = dailyDoc.id.toInt(),
                                            date = date,
                                            success = walk
                                        )
                                    )
                                }

                                dailyDoc.getString("knowledge")?.let { knowledge ->
                                    knowledgeDao.updateFirstZeroDateKnowledge(
                                        date = date,
                                        state = knowledge
                                    )
                                }

                            }

                            // 'items' 문서 안의 Map 필드들을 가져오기
                            val itemsSnapshot = db
                                .collection("users")
                                .document(it.uid)
                                .collection("dataCollection")
                                .document("items")
                                .get()
                                .await()

                            val itemsMap = itemsSnapshot.data ?: emptyMap()

                            for ((itemId, itemData) in itemsMap) {
                                if (itemData is Map<*, *>) {
                                    val date = itemData["date"] as? String ?: continue
                                    val size = (itemData["size"] as? String)?.toFloatOrNull() ?: continue
                                    val x = (itemData["x"] as? String)?.toFloatOrNull() ?: continue
                                    val y = (itemData["y"] as? String)?.toFloatOrNull() ?: continue

                                    itemDao.updateItemData(
                                        id = itemId.toInt(),
                                        date = date,
                                        x = x,
                                        y = y,
                                        size = size
                                    )
                                }
                            }

                            val areasSnapshot = db
                                .collection("users")
                                .document(it.uid)
                                .collection("dataCollection")
                                .document("areas")
                                .get()
                                .await()

                            val areasMap = areasSnapshot.data ?: emptyMap()

                            for ((areaId, areaData) in areasMap) {
                                if (areaData is Map<*, *>) {
                                    val date = areaData["date"] as? String ?: continue

                                    areaDao.updateAreaData(
                                        id = areaId.toInt(),
                                        date = date,
                                    )
                                }
                            }

                            val patsSnapshot = db
                                .collection("users")
                                .document(it.uid)
                                .collection("dataCollection")
                                .document("pats")
                                .get()
                                .await()

                            val patsMap = patsSnapshot.data ?: emptyMap()

                            for ((patId, patData) in patsMap) {
                                if (patData is Map<*, *>) {
                                    val date = patData["date"] as? String ?: continue
                                    val love = patData["love"] as? String ?: continue
                                    val size = (patData["size"] as? String)?.toFloatOrNull() ?: continue
                                    val x = (patData["x"] as? String)?.toFloatOrNull() ?: continue
                                    val y = (patData["y"] as? String)?.toFloatOrNull() ?: continue
                                    val gameCount = (patData["gameCount"] as? String)?.toIntOrNull() ?: continue
                                    val effect = (patData["effect"] as? String)?.toIntOrNull() ?: continue

                                    patDao.updatePatData(
                                        id = patId.toIntOrNull() ?: continue,
                                        date = date,
                                        love = love.toInt(),
                                        x = x,
                                        y = y,
                                        size = size,
                                        gameCount = gameCount,
                                        effect = effect
                                    )
                                }
                            }

                            //sudoku 서브컬렉션
                            val sudokuDoc = db
                                .collection("users")
                                .document(it.uid)
                                .collection("dataCollection")
                                .document("sudoku")
                                .get()
                                .await()

                            if (sudokuDoc.exists()) {
                                val level = sudokuDoc.getString("level")
                                val state = sudokuDoc.getString("state")
                                val sudokuBoard = sudokuDoc.getString("sudokuBoard")
                                val sudokuFirstBoard = sudokuDoc.getString("sudokuFirstBoard")
                                val sudokuMemoBoard = sudokuDoc.getString("sudokuMemoBoard")
                                val time = sudokuDoc.getString("time")
                                sudokuDao.update(id = "sudokuBoard", value = sudokuBoard)
                                sudokuDao.update(id = "sudokuFirstBoard", value = sudokuFirstBoard)
                                sudokuDao.update(id = "sudokuMemoBoard", value = sudokuMemoBoard)
                                sudokuDao.update(id = "time", value = time)
                                sudokuDao.update(id = "level", value = level)
                                sudokuDao.update(id = "state", value = state)
                            }

                            //letter 서브컬렉션
                            val lettersSnapshot = db
                                .collection("users")
                                .document(it.uid)
                                .collection("dataCollection")
                                .document("letters")
                                .get()
                                .await()

                            val lettersMap = lettersSnapshot.data ?: emptyMap()

                            for ((letterId, letterData) in lettersMap) {
                                //수정
                                if (letterData is Map<*, *>) {
                                    val date = letterData["date"] as? String ?: continue
                                    val title = letterData["title"] as? String ?: continue
                                    val message = letterData["message"] as? String ?: continue
                                    val link = letterData["link"] as? String ?: continue
                                    val reward = letterData["reward"] as? String ?: continue
                                    val amount = letterData["amount"] as? String ?: continue
                                    val state = letterData["state"] as? String ?: continue

                                    letterDao.insert(
                                        Letter(
                                            id = letterId.toInt(),
                                            date = date,
                                            title = title,
                                            message = message,
                                            link = link,
                                            reward = reward,
                                            amount = amount,
                                            state = state,
                                        )
                                    )
                                }
                            }

                        } else {
                            Log.w("login", "Firestore에 유저 문서가 없습니다")
                            postSideEffect(LoginSideEffect.Toast("유저 정보를 찾을 수 없습니다"))
                            return@intent
                        }
                    } catch (e: Exception) {
                        Log.e("login", "Firestore에서 유저 문서 가져오기 실패", e)
                        postSideEffect(LoginSideEffect.Toast("유저 정보 로딩 실패"))
                        return@intent
                    }

                    reduce {
                        state.copy(
                            dialog = "explanation"
                        )
                    }

                }

            }

        } catch (e: Exception) {
            Log.e("login", "뷰모델 로그인 실패", e)
            postSideEffect(LoginSideEffect.Toast("로그인 실패: ${e.localizedMessage}"))
        } finally {
            reduce { state.copy(isLoggingIn = false) }
        }
    }

    fun onNavigateToMainScreen() = intent {
        postSideEffect(LoginSideEffect.NavigateToMainScreen)
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

            // 🎯 ✅ 모든 처리 완료 후 상태 변경
            reduce {
                state.copy(loginState = "login")
            }
            Log.e("Firestore", "letter 확인")

        } catch (e: Exception) {
            Log.e("Firestore", "letter 처리 실패", e)
            reduce {
                state.copy(loginState = "login")
            }
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
                postSideEffect(LoginSideEffect.Toast("인터넷 연결 오류"))
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

    fun dialogChange(string: String) = intent {
        reduce {
            state.copy(
                dialog = string
            )
        }
    }

}

@Immutable
data class LoginState(
    val userData: List<User> = emptyList(),
    val isLoggingIn:Boolean = false,
    val loginState: String = "",
    val dialog: String = ""
)

//상태와 관련없는 것
sealed interface LoginSideEffect{
    class Toast(val message:String): LoginSideEffect
    data object NavigateToMainScreen: LoginSideEffect

}