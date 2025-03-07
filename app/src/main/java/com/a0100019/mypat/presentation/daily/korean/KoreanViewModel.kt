package com.a0100019.mypat.presentation.daily.korean

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
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
        val koreanOpenDataList = koreanDao.getOpenKoreanIdiomData()
        val koreanStarList = koreanDao.getStarKoreanIdiomData()

        reduce {
            state.copy(
                koreanOpenDataList = koreanOpenDataList,
                koreanStarDataList = koreanStarList

            )
        }
    }

    fun onKoreanClick(koreanIdiom: KoreanIdiom) = intent {

    }

    fun onReadyClick(koreanIdiom: KoreanIdiom) = intent {

    }

}


@Immutable
data class KoreanState(
    val userData: List<User> = emptyList(),
    val koreanOpenDataList: List<KoreanIdiom> = emptyList(),
    val koreanStarDataList: List<KoreanIdiom> = emptyList(),

    val clickKoreanData: KoreanIdiom? = null,
    val todayKoreanData: KoreanIdiom = KoreanIdiom(),
    val filter: String = "일반",
)


//상태와 관련없는 것
sealed interface KoreanSideEffect{
    class Toast(val message:String): KoreanSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}