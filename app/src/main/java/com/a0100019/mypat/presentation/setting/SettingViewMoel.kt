package com.a0100019.mypat.presentation.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.diary.getDiaryInitialData
import com.a0100019.mypat.data.room.english.English
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.english.getEnglishInitialData
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.item.getItemInitialData
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.koreanIdiom.getKoreanIdiomInitialData
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.data.room.letter.LetterDao
import com.a0100019.mypat.data.room.letter.getLetterInitialData
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.area.getAreaInitialData
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.pat.getPatInitialData
import com.a0100019.mypat.data.room.sudoku.Sudoku
import com.a0100019.mypat.data.room.sudoku.SudokuDao
import com.a0100019.mypat.data.room.sudoku.getSudokuInitialData
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.user.getUserInitialData
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.data.room.walk.getWalkInitialData
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.data.room.world.WorldDao
import com.a0100019.mypat.data.room.world.getWorldInitialData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
import java.time.LocalDateTime
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
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
) : ViewModel(), ContainerHost<SettingState, SettingSideEffect> {

    override val container: Container<SettingState, SettingSideEffect> = container(
        initialState = SettingState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(SettingSideEffect.Toast(message = throwable.message.orEmpty()))
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
        val itemDataList = itemDao.getAllItemData()
        val patDataList = patDao.getAllPatData()
        val worldDataList = worldDao.getAllWorldData()
        val letterDataList = letterDao.getAllLetterData()
        val walkDataList = walkDao.getAllWalkData()
        val englishDataList = englishDao.getOpenEnglishData()
        val koreanIdiomDataList = koreanIdiomDao.getOpenKoreanIdiomData()
        val diaryDataList = diaryDao.getAllDiaryData()
        val sudokuDataList = sudokuDao.getAllSudokuData()
        val areaDataList = areaDao.getAllAreaData()

        reduce {
            state.copy(
                userDataList = userDataList,
                itemDataList = itemDataList,
                patDataList = patDataList,
                worldDataList = worldDataList,
                letterDataList = letterDataList,
                walkDataList = walkDataList,
                englishDataList = englishDataList,
                koreanIdiomDataList = koreanIdiomDataList,
                diaryDataList = diaryDataList,
                sudokuDataList = sudokuDataList,
                areaDataList = areaDataList
            )
        }
    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                settingSituation = "",
                editText = "",
                clickLetterData = Letter()
            )
        }
    }

//    fun onTermsClick() = intent {
//        try {
//            val uri = FirebaseStorage.getInstance()
//                .reference.child("sample.png")
//                .downloadUrl.await()
//
//            reduce {
//                state.copy(imageUrl = uri.toString())
//            }
//        } catch (e: Exception) {
//            // 실패 처리 가능
//        }
//    }

    fun onSituationChange(situation: String) = intent {
        reduce {
            state.copy(
                settingSituation = situation
            )
        }
    }

    private fun onSignOutClick() = intent {

        FirebaseAuth.getInstance().signOut()
        // 현재 사용자 null이면 로그아웃 성공
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // ✅ 로그아웃 성공
            // 사용자 데이터 삭제
//            userDao.update(id = "auth", value = "0")
            roomDataClear()
            postSideEffect(SettingSideEffect.Toast("로그아웃 되었습니다"))
            postSideEffect(SettingSideEffect.NavigateToLoginScreen)
        } else {
            // ❌ 로그아웃 실패
            postSideEffect(SettingSideEffect.Toast("로그아웃에 실패했습니다"))
        }

    }

    fun dataSave() = intent {

        try {
            // ... 전체 dataSave() 내용
        val db = Firebase.firestore
        val userId = state.userDataList.find { it.id == "auth" }!!.value
        val userDataList = state.userDataList
        val itemDataList = state.itemDataList
        val patDataList = state.patDataList
        val worldDataList = state.worldDataList
        val walkDataList = state.walkDataList
        val englishDataList = state.englishDataList
        val diaryDataList = state.diaryDataList
        val koreanIdiomDataList = state.koreanIdiomDataList
        val letterDataList = state.letterDataList
        val sudokuDataList = state.sudokuDataList
        val areaDataList = state.areaDataList

        val batch = db.batch()

        val userData = mapOf(
            "cash" to userDataList.find { it.id == "money"}!!.value2,
            "money" to userDataList.find { it.id == "money"}!!.value,

            "community" to mapOf(
                "ban" to userDataList.find { it.id == "community"}!!.value3,
                "like" to userDataList.find { it.id == "community"}!!.value,
                "warning" to userDataList.find {it.id == "community"}!!.value2
            ),

            "date" to mapOf(
                "firstDate" to userDataList.find { it.id == "date"}!!.value,
                "totalDate" to userDataList.find { it.id == "date"}!!.value2,
                "lastDate" to userDataList.find { it.id == "date"}!!.value3
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

        //월드 데이터
        val worldMap = worldDataList.drop(1)
            .mapIndexed { index, data ->

                if(data.type == "pat") {
                    val patData = patDataList.find { it.id == data.value.toInt() }
                    // index는 0부터 시작하니까 +1 해서 문자열로 만듦
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
                    // index는 0부터 시작하니까 +1 해서 문자열로 만듦
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

        val finalData = userData + mapOf("world" to worldMap)
        val userDocRef = Firebase.firestore.collection("users").document(userId)
        batch.set(userDocRef, finalData, SetOptions.merge()) // 필드 기준 병합 저장

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

        for (id in 1..userDataList.find { it.id == "date" }!!.value2.toInt()) {
            val docRef = dailyCollectionRef.document(id.toString())

            // diary는 항상 존재
            val diary = diaryDataList.find { it.id == id }!!
            val walk = walkDataList.find { it.id == id }!!.success

            // state 구성 (둘 중 하나라도 null이면 제외)
            val englishState = englishDataList.find { it.id == id }?.state
            val idiomState = koreanIdiomDataList.find { it.id == id }?.state

            val data = mutableMapOf<String, Any>(
                "date" to diary.date,
                "diary" to mapOf(
                    "emotion" to diary.emotion,
                    "state" to diary.state,
                    "contents" to diary.contents
                ),
                "walk" to walk
            )

            if (englishState != null && idiomState != null) {
                data["state"] = mapOf(
                    "english" to englishState,
                    "koreanIdiom" to idiomState
                )
            }

            batch.set(docRef, data)
        }

        Log.d("Firestore", "batch.commit() 직전")

        // 전체 커밋 실행
        batch.commit()
            .addOnSuccessListener {
                onSignOutClick()
            }
            .addOnFailureListener {
                Log.e("Firestore", "저장 실패", it)
            }

        } catch (e: Exception) {
            Log.e("Firestore", "예외 발생", e)
        }
    }

    fun onAccountDeleteClick() = intent {
        if(state.editText == "계정삭제"){
            reduce {
                state.copy(
                    settingSituation = ""
                )
            }

            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            val userDocRef =
                db.collection("users").document(state.userDataList.find { it.id == "auth" }!!.value)
            val subCollections =
                listOf("daily", "dataCollection")

            try {
                // 1. 서브컬렉션 안의 문서 삭제
                for (sub in subCollections) {
                    val subColRef = userDocRef.collection(sub)
                    val documents = subColRef.get().await().documents
                    for (doc in documents) {
                        doc.reference.delete().await()
                    }
                }

                // 2. 마지막으로 사용자 문서 삭제
                userDocRef.delete().await()

                // 2. Authentication 계정 삭제
                auth.currentUser?.delete()
                    ?.addOnSuccessListener {
                        viewModelScope.launch {
                            roomDataClear()
                            postSideEffect(SettingSideEffect.Toast("계정이 삭제되었습니다."))
                            postSideEffect(SettingSideEffect.NavigateToLoginScreen)
                        }
                        Log.d("Auth", "계정 삭제 완료")
                    }
                    ?.addOnFailureListener {
                        Log.e("Auth", "계정 삭제 실패", it)
                    }

                Log.d("Firestore", "사용자 전체 삭제 완료")
            } catch (e: Exception) {
                Log.e("Firestore", "삭제 실패", e)
            }

        } else {
            postSideEffect(SettingSideEffect.Toast("[계정삭제]를 입력해주세요."))
        }

    }

    private fun roomDataClear() = intent {
        // 모든 유저 데이터 삭제
        userDao.deleteAllUsers()
        // 초기 데이터 삽입
        val initialUserData = getUserInitialData()
        userDao.insertAll(initialUserData)

        diaryDao.deleteAllDiary()
        diaryDao.resetDiaryPrimaryKey()
        val initialDiaryData = getDiaryInitialData()
        diaryDao.insertAll(initialDiaryData)

        englishDao.deleteAllEnglish()
        englishDao.resetEnglishPrimaryKey()
        val initialEnglishData = getEnglishInitialData()
        englishDao.insertAll(initialEnglishData)

        itemDao.deleteAllItems()
        itemDao.resetItemPrimaryKey()
        val initialItemData = getItemInitialData()
        itemDao.insertAll(initialItemData)

        koreanIdiomDao.deleteAllKoreanIdioms()
        koreanIdiomDao.resetKoreanIdiomPrimaryKey()
        val initialKoreanIdiomData = getKoreanIdiomInitialData()
        koreanIdiomDao.insertAll(initialKoreanIdiomData)

        letterDao.deleteAllLetters()
        letterDao.resetLetterPrimaryKey()
        val initialLetterData = getLetterInitialData()
        letterDao.insertAll(initialLetterData)

        patDao.deleteAllPats()
        patDao.resetPatPrimaryKey()
        val initialPatData = getPatInitialData()
        patDao.insertAll(initialPatData)

        sudokuDao.deleteAllSudoku()
        val initialSudokuData = getSudokuInitialData()
        sudokuDao.insertAll(initialSudokuData)

        walkDao.deleteAllWalks()
        walkDao.resetWalkPrimaryKey()
        val initialWalkData = getWalkInitialData()
        walkDao.insertAll(initialWalkData)

        worldDao.deleteAllWorlds()
        worldDao.resetWorldPrimaryKey()
        val initialWorldData = getWorldInitialData()
        worldDao.insertAll(initialWorldData)

        areaDao.deleteAllAreas()
        areaDao.resetAreaPrimaryKey()
        val initialAreaData = getAreaInitialData()
        areaDao.insertAll(initialAreaData)

    }

    fun onCouponConfirmClick() = intent {
        val db = Firebase.firestore
        val couponText = state.editText // 사용자가 입력한 쿠폰 코드
        val userId = state.userDataList.find { it.id == "auth" }!!.value

        db.collection("users").document(userId).collection("code").document("coupon")
            .get()
            .addOnSuccessListener { couponDocument ->
                if (couponDocument != null && couponDocument.contains(couponText)) {
                    // 이미 사용한 쿠폰
                    viewModelScope.launch {
                        postSideEffect(SettingSideEffect.Toast("이미 사용한 쿠폰 번호입니다."))
                    }
                } else {
                    // 아직 사용하지 않은 쿠폰이므로, 유효한 쿠폰인지 확인
                    db.collection("code").document("coupon")
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.contains(couponText)) {
                                val couponData = document.get(couponText) as? Map<*, *>
                                val reward = couponData?.get("reward") as? String
                                val type = couponData?.get("type") as? String
                                val amount = couponData?.get("amount") as? String

                                Log.d("Coupon", "내용: $reward, 금액: $amount")

                                val newCouponMap = mapOf(
                                    couponText to mapOf(
                                        "reward" to reward,
                                        "type" to type,
                                        "amount" to amount
                                    )
                                )

                                if(type == "all"){

                                    db.collection("users").document(userId)
                                        .collection("code").document("coupon")
                                        .set(newCouponMap, SetOptions.merge())
                                        .addOnSuccessListener {
                                            viewModelScope.launch {
                                                if (reward == "money") {
                                                    userDao.update(
                                                        id = "money",
                                                        value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + amount!!.toInt()).toString()
                                                    )
                                                } else {
                                                    userDao.update(
                                                        id = "money",
                                                        value2 = (state.userDataList.find { it.id == "money" }!!.value2.toInt() + amount!!.toInt()).toString()
                                                    )
                                                }
                                                postSideEffect(SettingSideEffect.Toast("쿠폰 사용 : $reward +$amount"))
                                                onCloseClick()
                                                loadData()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Coupon", "쿠폰 저장 실패", e)
                                        }

                                } else if(type == "one"){

                                    db.collection("users").document(userId)
                                        .collection("code").document("coupon")
                                        .set(newCouponMap, SetOptions.merge())
                                        .addOnSuccessListener {
                                            // 필드 삭제 먼저 수행
                                            val deleteMap = mapOf<String, Any>(
                                                couponText to FieldValue.delete()
                                            )

                                            db.collection("code").document("coupon")
                                                .update(deleteMap)
                                                .addOnSuccessListener {
                                                    Log.d("Coupon", "기존 필드 $couponText 삭제 성공")
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e("Coupon", "기존 필드 $couponText 삭제 실패", e)
                                                }

                                            viewModelScope.launch {
                                                if (reward == "money") {
                                                    userDao.update(
                                                        id = "money",
                                                        value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + amount!!.toInt()).toString()
                                                    )
                                                } else {
                                                    userDao.update(
                                                        id = "money",
                                                        value2 = (state.userDataList.find { it.id == "money" }!!.value2.toInt() + amount!!.toInt()).toString()
                                                    )
                                                }
                                                postSideEffect(SettingSideEffect.Toast("쿠폰 사용 : $reward +$amount"))
                                                onCloseClick()
                                                loadData()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Coupon", "쿠폰 저장 실패", e)
                                        }


                                }

                            } else {
                                // 존재하지 않는 쿠폰
                                viewModelScope.launch {
                                    postSideEffect(SettingSideEffect.Toast("존재하지 않는 쿠폰 번호입니다."))
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Coupon", "쿠폰 조회 실패", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Coupon", "사용자 쿠폰 조회 실패", e)
            }


    }

    fun onSettingTalkConfirmClick() = intent {
        val db = Firebase.firestore
        db.collection("settingTalk")
            .document(state.userDataList.find { it.id == "auth" }!!.value)
            .set(
                mapOf(LocalDateTime.now().toString() to state.editText),
                SetOptions.merge() // 기존 데이터 유지 + 필드 추가
            )
            .addOnSuccessListener {
                viewModelScope.launch {
                    postSideEffect(SettingSideEffect.Toast("전송되었습니다."))
                    onCloseClick()
                }
            }
    }


    fun clickLetterDataChange(letterId: Int) = intent {
        if(letterId != 0) {

            val clickLetterData = state.letterDataList.find { it.id == letterId }!!


            reduce {
                state.copy(
                    clickLetterData = clickLetterData
                    )
            }

        } else {

            reduce {
                state.copy(
                    clickLetterData = Letter(),
                )
            }
        }

    }

    fun onLetterLinkClick() = intent {
        val url = state.clickLetterData.link
        postSideEffect(SettingSideEffect.OpenUrl(url))
    }

    fun onLetterCloseClick() = intent {
        clickLetterDataChange(0)
        loadData()
    }

    fun onLetterConfirmClick() = intent {

        val letterData = state.clickLetterData

        if(letterData.state == "open"){
            if (letterData.reward == "money") {
                userDao.update(
                    id = "money",
                    value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + letterData.amount.toInt()).toString()
                )
            } else {
                userDao.update(
                    id = "money",
                    value2 = (state.userDataList.find { it.id == "money" }!!.value2.toInt() + letterData.amount.toInt()).toString()
                )
            }
            postSideEffect(SettingSideEffect.Toast("보상 획득 : ${letterData.reward} +${letterData.amount}"))

            letterData.state = "read"
            letterDao.update(letterData)
            clickLetterDataChange(0)
            loadData()
        } else {
            clickLetterDataChange(0)
            loadData()
        }

    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onEditTextChange(editText: String) = blockingIntent {
        reduce {
            state.copy(editText = editText)
        }
    }

}


@Immutable
data class SettingState(
    val userDataList: List<User> = emptyList(),
    val isLoggingIn:Boolean = false,
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val worldDataList: List<World> = emptyList(),
    val walkDataList: List<Walk> = emptyList(),
    val englishDataList: List<English> = emptyList(),
    val koreanIdiomDataList: List<KoreanIdiom> = emptyList(),
    val diaryDataList: List<Diary> = emptyList(),
    val settingSituation: String = "",
    val imageUrl: String = "",
    val editText: String = "",
    val clickLetterData: Letter = Letter(),
    val letterDataList: List<Letter> = emptyList(),
    val sudokuDataList: List<Sudoku> = emptyList(),
    val areaDataList: List<Area> = emptyList()
    )


sealed interface SettingSideEffect {
    class Toast(val message: String) : SettingSideEffect
    data object NavigateToLoginScreen : SettingSideEffect
    data class OpenUrl(val url: String) : SettingSideEffect

}
