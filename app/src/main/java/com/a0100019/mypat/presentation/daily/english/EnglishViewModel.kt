package com.a0100019.mypat.presentation.daily.english

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.english.English
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.daily.korean.KoreanSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class EnglishViewModel @Inject constructor(
    private val userDao: UserDao,
    private val englishDao: EnglishDao
) : ViewModel(), ContainerHost<EnglishState, EnglishSideEffect> {

    override val container: Container<EnglishState, EnglishSideEffect> = container(
        initialState = EnglishState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(EnglishSideEffect.Toast(message = throwable.message.orEmpty()))
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

        val englishDataList = englishDao.getOpenEnglishData()

        reduce {
            state.copy(
                englishDataList = englishDataList
            )
        }

    }


    fun onSubmitClick() = intent {
        if(state.englishText == state.clickEnglishData!!.word) {

            val newClickEnglishData = state.clickEnglishData
            newClickEnglishData!!.state = "완료"

            englishDao.update(newClickEnglishData)

            reduce {
                state.copy(
                    clickEnglishData = null,
                    englishText = "",
                    clickEnglishDataState = ""
                )
            }

            //보상
            userDao.update(id = "money", value = (state.userData.find { it.id == "money" }!!.value.toInt() + 100).toString())

            postSideEffect(EnglishSideEffect.Toast("정답입니다 money+100"))
        } else {
            val newClickKoreanData = state.clickEnglishData
            newClickKoreanData!!.state = "오답"
            reduce {
                state.copy(
                    englishText = "",
                    clickEnglishData = newClickKoreanData,
                    clickEnglishDataState = "오답"
                )
            }
            postSideEffect(EnglishSideEffect.Toast("오답입니다."))
        }

    }

    fun onFilterClick() = intent {

        if(state.filter == "일반") {
            val englishStarList = englishDao.getStarEnglishData()
            reduce {
                state.copy(
                    filter = "별",
                    englishDataList = englishStarList
                )
            }
        } else {
            val englishDataList = englishDao.getOpenEnglishData()
            reduce {
                state.copy(
                    filter = "일반",
                    englishDataList = englishDataList
                )
            }
        }
    }

    fun onEnglishClick(english: English) = intent {
        reduce {
            state.copy(
                clickEnglishData = english,
                clickEnglishDataState = english.state
            )
        }
    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                clickEnglishData = null,
                clickEnglishDataState = ""
            )
        }
    }

    fun onFailDialogCloseClick() = intent {
        reduce {
            state.copy(
                clickEnglishDataState = "대기"
            )
        }
    }

    fun onStateChangeClick() = intent {

        val stateChangeEnglishData = state.clickEnglishData
        stateChangeEnglishData!!.state = if(stateChangeEnglishData.state == "별") "완료" else "별"
        englishDao.update(stateChangeEnglishData)

        val englishDataList = state.englishDataList
        val updatedList = englishDataList.map {
            if (it.id == stateChangeEnglishData.id) stateChangeEnglishData else it
        }

        reduce {
            state.copy(
                clickEnglishData = stateChangeEnglishData,
                clickEnglishDataState = stateChangeEnglishData.state,

                englishDataList = updatedList
            )
        }

    }


    //아이디 입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onEnglishTextChange(englishText: String) = blockingIntent {
        reduce {
            state.copy(englishText = englishText)
        }
    }



}


@Immutable
data class EnglishState(
    val userData: List<User> = emptyList(),
    val englishDataList: List<English> = emptyList(),

    val clickEnglishData: English? = null,
    val todayEnglishData: English = English(),
    val filter: String = "일반",
    val clickEnglishDataState: String = "",
    val englishText: String = "",

    )


//상태와 관련없는 것
sealed interface EnglishSideEffect{
    class Toast(val message:String): EnglishSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}