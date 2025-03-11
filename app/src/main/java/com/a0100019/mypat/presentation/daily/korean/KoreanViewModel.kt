package com.a0100019.mypat.presentation.daily.korean

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.store.StoreSideEffect
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
class KoreanViewModel @Inject constructor(
    private val userDao: UserDao,
    private val koreanDao: KoreanIdiomDao,
) : ViewModel(), ContainerHost<KoreanState, KoreanSideEffect> {

    override val container: Container<KoreanState, KoreanSideEffect> = container(
        initialState = KoreanState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(KoreanSideEffect.Toast(message = throwable.message.orEmpty()))
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
        val koreanDataList = koreanDao.getOpenKoreanIdiomData()

        reduce {
            state.copy(
                koreanDataList = koreanDataList,

            )
        }
    }

    fun onSubmitClick() = intent {
        if(state.koreanText == state.clickKoreanData!!.korean) {

            val newClickKoreanData = state.clickKoreanData
            newClickKoreanData!!.state = "완료"

            koreanDao.update(newClickKoreanData)

            reduce {
                state.copy(
                    clickKoreanData = null,
                    koreanText = "",
                    clickKoreanDataState = ""
                )
            }
            postSideEffect(KoreanSideEffect.Toast("정답입니다."))
        } else {
            postSideEffect(KoreanSideEffect.Toast("오답입니다."))
        }

    }

    fun onFilterClick() = intent {

        if(state.filter == "일반") {
            val koreanStarList = koreanDao.getStarKoreanIdiomData()
            reduce {
                state.copy(
                    filter = "별",
                    koreanDataList = koreanStarList
                )
            }
        } else {
            val koreanDataList = koreanDao.getOpenKoreanIdiomData()
            reduce {
                state.copy(
                    filter = "일반",
                    koreanDataList = koreanDataList
                )
            }
        }
    }

    fun onKoreanClick(koreanIdiom: KoreanIdiom) = intent {
        reduce {
            state.copy(
                clickKoreanData = koreanIdiom,
                clickKoreanDataState = koreanIdiom.state
            )
        }
    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                clickKoreanData = null,
                clickKoreanDataState = ""
            )
        }
    }

    fun onStateChangeClick() = intent {

        val stateChangeKoreanData = state.clickKoreanData
        stateChangeKoreanData!!.state = if(stateChangeKoreanData.state == "별") "완료" else "별"
        koreanDao.update(stateChangeKoreanData)

        val koreanDataList = state.koreanDataList
        val updatedList = koreanDataList.map {
            if (it.id == stateChangeKoreanData.id) stateChangeKoreanData else it
        }

        reduce {
            state.copy(
                clickKoreanData = stateChangeKoreanData,
                clickKoreanDataState = stateChangeKoreanData.state,

                koreanDataList = updatedList
            )
        }

    }


    //아이디 입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onKoreanTextChange(koreanText: String) = blockingIntent {
        reduce {
            state.copy(koreanText = koreanText)
        }
    }


}


@Immutable
data class KoreanState(
    val userData: List<User> = emptyList(),
    val koreanDataList: List<KoreanIdiom> = emptyList(),

    val clickKoreanData: KoreanIdiom? = null,
    val todayKoreanData: KoreanIdiom = KoreanIdiom(),
    val filter: String = "일반",
    val clickKoreanDataState: String = "",
    val koreanText: String = "",

)


//상태와 관련없는 것
sealed interface KoreanSideEffect{
    class Toast(val message:String): KoreanSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}