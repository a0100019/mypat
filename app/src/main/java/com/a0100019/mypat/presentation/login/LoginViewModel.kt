package com.a0100019.mypat.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.letter.LetterDao
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.sudoku.Sudoku
import com.a0100019.mypat.data.room.sudoku.SudokuDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.data.room.world.WorldDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.tasks.await
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
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

    fun dialogChangeClick(string: String) = intent {
        reduce {
            state.copy(
                dialogState = string
            )
        }
    }

    fun onGuestLoginClick() = intent {
        userDao.update(id = "auth", value = "guest")
        postSideEffect(LoginSideEffect.NavigateToMainScreen)
        reduce {
            state.copy(
                dialogState = ""
            )
        }
    }

    fun onGoogleLoginClick(idToken: String) = intent {
        Log.e("login", "idToken = $idToken")

        if (state.isLoggingIn) return@intent

        reduce { state.copy(isLoggingIn = true) }

        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            val user = authResult.user
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

            Log.e("login", "user = $user, isNewUser = $isNewUser")

            user?.let {
                if (isNewUser) {
                    // 🔹 신규 사용자일 때만 실행되는 코드
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

                            val money = userDoc.getString("money")
                            val cash = userDoc.getString("cash")
                            userDao.update(id = "money", value = money, value2 = cash)

                            val communityMap = userDoc.get("community") as Map<String, String>
                            val ban = communityMap["ban"]
                            val like = communityMap["like"]
                            val warning = communityMap["warning"]
                            userDao.update(id = "community", value = like, value2 = warning, value3 = ban)

                            val firstDate = userDoc.getString("firstDate")
                            val totalDate = userDoc.getString("totalDate")
                            userDao.update(id = "date", value2 = totalDate, value3 = firstDate)

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
//                            val openItem = itemMap["openItem"]
                            val openItemSpace = itemMap["openItemSpace"]
                            val useItem = itemMap["useItem"]
                            userDao.update(id = "item", value2 = openItemSpace, value3 = useItem)

                            val map = userDoc.getString("map")
                            worldDao.insert(World(id = 1, value = map.toString(), type = "map"))

                            val name = userDoc.getString("name")
                            userDao.update(id = "name", value = name)
                            val lastLogIn = userDoc.getString("lastLogIn")
                            val tag = userDoc.getString("tag")
                            userDao.update(id = "auth", value = it.uid, value2 = tag, value3 = lastLogIn)

                            val patMap = userDoc.get("pat") as Map<String, String>
                            val openPatSpace = patMap["openPatSpace"]
                            val usePat = patMap["usePat"]
                            userDao.update(id = "pat", value2 = openPatSpace, value3 = usePat)

                            val worldMap = userDoc.get("world") as Map<String, Map<String, String>>
                            for ((key, innerMap) in worldMap) {
                                val id = innerMap["id"]
                                val size = innerMap["size"]
                                val type = innerMap["type"]
                                val x = innerMap["x"]
                                val y = innerMap["y"]

                                worldDao.insert(World(value = id.toString(), type = type.toString()))
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
                                val date = dailyDoc.getString("date")

                                val diaryMap = dailyDoc.get("diary") as Map<String, String>
                                val diaryContents = diaryMap["contents"]
                                val diaryEmotion = diaryMap["emotion"]
                                val diaryState = diaryMap["state"]
                                diaryDao.insert(Diary(id = dailyDoc.id.toInt(), date = date.toString(), emotion = diaryEmotion.toString(), state = diaryState.toString(), contents = diaryContents.toString()))

                                val stateMap = dailyDoc.get("state") as Map<String, String>
                                val englishState = stateMap["english"]
                                val koreanIdiomState = stateMap["koreanIdiom"]
                                englishDao.updateDateAndState(id = dailyDoc.id.toInt(), date = date.toString(), state = englishState.toString())
                                koreanIdiomDao.updateDateAndState(id = dailyDoc.id.toInt(), date = date.toString(), state = koreanIdiomState.toString())

                                val walkMap = dailyDoc.get("walk") as Map<String, String>
                                val walkCount = walkMap["count"]
                                val walkSteps = walkMap["steps"]
                                walkDao.insert(Walk(id = dailyDoc.id.toInt(), date = date.toString(), count = walkCount!!.toInt(), steps = walkSteps!!.toInt()))

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
                                    val love = (patData["love"] as? String)?.toIntOrNull() ?: continue
                                    val size = (patData["size"] as? String)?.toFloatOrNull() ?: continue
                                    val x = (patData["x"] as? String)?.toFloatOrNull() ?: continue
                                    val y = (patData["y"] as? String)?.toFloatOrNull() ?: continue

                                    patDao.updatePatData(
                                        id = patId.toIntOrNull() ?: continue,
                                        date = date,
                                        love = love,
                                        x = x,
                                        y = y,
                                        size = size
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
                            val letterDoc = db
                                .collection("users")
                                .document(it.uid)
                                .collection("dataCollection")
                                .document("letters")
                                .get()
                                .await()

                            val lettersMap = patsSnapshot.data ?: emptyMap()

                            for ((letterId, letterData) in lettersMap) {
                                //수정
                                if (letterData is Map<*, *>) {
                                    val date = patData["date"] as? String ?: continue
                                    val love = (patData["love"] as? String)?.toIntOrNull() ?: continue
                                    val size = (patData["size"] as? String)?.toFloatOrNull() ?: continue
                                    val x = (patData["x"] as? String)?.toFloatOrNull() ?: continue
                                    val y = (patData["y"] as? String)?.toFloatOrNull() ?: continue

                                    patDao.updatePatData(
                                        id = patId.toIntOrNull() ?: continue,
                                        date = date,
                                        love = love,
                                        x = x,
                                        y = y,
                                        size = size
                                    )
                                }
                            }

                            if (letterDoc.exists()) {
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


                        } else {
                            Log.w("login", "Firestore에 유저 문서가 없습니다")
                            postSideEffect(LoginSideEffect.Toast("유저 정보를 찾을 수 없습니다"))
                        }
                    } catch (e: Exception) {
                        Log.e("login", "Firestore에서 유저 문서 가져오기 실패", e)
                        postSideEffect(LoginSideEffect.Toast("유저 정보 로딩 실패"))
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("login", "뷰모델 로그인 실패", e)
            postSideEffect(LoginSideEffect.Toast("로그인 실패: ${e.localizedMessage}"))
        } finally {
            reduce { state.copy(isLoggingIn = false) }
            onNavigateToMainScreen()
        }
    }


    fun onNavigateToMainScreen() = intent {
        postSideEffect(LoginSideEffect.NavigateToMainScreen)
    }



}




@Immutable
data class LoginState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList(),
    val isLoggingIn:Boolean = false,
    val dialogState: String = "",
    val loginState: String = ""
)


//상태와 관련없는 것
sealed interface LoginSideEffect{
    class Toast(val message:String): LoginSideEffect
    data object NavigateToMainScreen: LoginSideEffect

}