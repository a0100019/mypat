package com.a0100019.mypat.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.data.room.letter.LetterDao
import com.a0100019.mypat.data.room.area.AreaDao
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val areaDao: AreaDao
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

        if(loginState == "0") {
            reduce {
                state.copy(
                    loginState = "unLogin"
                )
            }
        } else {
            reduce {
                state.copy(
                    loginState = "login"
                )
            }
        }

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

                    userDao.update(id = "walk", value2 = "-1")

                    val userRef = db.collection("users").document(it.uid)
                    userRef.set(mapOf("online" to "1"), SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("login", "online 필드가 1로 설정됨 (set + merge)")
                        }
                        .addOnFailureListener { e ->
                            Log.e("login", "online 필드 설정 실패", e)
                        }

                    Log.e("login", "신규 사용자입니다")
                    postSideEffect(LoginSideEffect.Toast("처음 오신 것을 환영합니다!"))

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

                            val communityMap = userDoc.get("community") as Map<String, String>
                            val ban = communityMap["ban"]
                            val like = communityMap["like"]
                            val warning = communityMap["warning"]
                            userDao.update(id = "community", value = like, value2 = warning, value3 = ban)

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

                            val area = userDoc.getString("area")
                            worldDao.insert(World(id = 1, value = area.toString(), type = "area"))
                            val name = userDoc.getString("name")
                            userDao.update(id = "name", value = name)
                            val lastLogIn = userDoc.getString("lastLogIn")
                            val tag = userDoc.getString("tag")
                            userDao.update(id = "auth", value = it.uid, value2 = tag, value3 = lastLogIn)

                            val walkMap = userDoc.get("walk") as Map<String, String>
                            val saveWalk = walkMap["saveWalk"]
                            val totalWalk = walkMap["totalWalk"]
                            userDao.update(id = "walk", value = saveWalk, value3 = totalWalk)

                            val worldMap = userDoc.get("world") as Map<String, Map<String, String>>
                            for ((index, innerMap) in worldMap) {
                                val id = innerMap["id"]
                                val size = innerMap["size"]
                                val type = innerMap["type"]
                                val x = innerMap["x"]
                                val y = innerMap["y"]

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

                            for (dailyDoc in dailySubCollectionSnapshot.documents) {
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

                                val walk = dailyDoc.getString("walk") ?: ""
                                walkDao.insert(Walk(id = dailyDoc.id.toInt(), date = date, success = walk))
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

                                    patDao.updatePatData(
                                        id = patId.toIntOrNull() ?: continue,
                                        date = date,
                                        love = love.toInt(),
                                        x = x,
                                        y = y,
                                        size = size,
                                        gameCount = gameCount
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
                        }
                    } catch (e: Exception) {
                        Log.e("login", "Firestore에서 유저 문서 가져오기 실패", e)
                        postSideEffect(LoginSideEffect.Toast("유저 정보 로딩 실패"))
                    }
                }
                onNavigateToMainScreen()
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

    fun newLetterGet() = intent {
        val letterDocRef = Firebase.firestore
            .collection("code")
            .document("letter")

        letterDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val letterMap = documentSnapshot.data as Map<String, Map<String, String>>
                    letterMap.forEach { (key, value) ->
                        val id = key.toIntOrNull() ?: return@forEach
                        val amount = value["amount"].orEmpty()
                        val date = value["date"].orEmpty()
                        val link = value["link"].orEmpty()
                        val message = value["message"].orEmpty()
                        val reward = value["reward"].orEmpty()
                        val state = value["state"].orEmpty()
                        val title = value["title"].orEmpty()

                        val letter = Letter(
                            id = id,
                            amount = amount,
                            date = date,
                            link = link,
                            message = message,
                            reward = reward,
                            state = state,
                            title = title
                        )

                        viewModelScope.launch {
                            letterDao.insertIgnore(letter)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "letter 문서 가져오기 실패", e)
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