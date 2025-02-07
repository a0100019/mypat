package com.a0100019.mypat.presentation.daily.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val userDao: UserDao,
    private val diaryDao: DiaryDao
) : ViewModel(), ContainerHost<DiaryState, DiarySideEffect> {

    override val container: Container<DiaryState, DiarySideEffect> = container(
        initialState = DiaryState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(DiarySideEffect.Toast(message = throwable.message.orEmpty()))
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
// 병렬로 실행할 작업들을 viewModelScope.launch로 묶음
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val userDataList = userDao.getAllUserData()
                val diaryDataList = diaryDao.getAllDiaryData()

                // UI 상태 업데이트 (Main Dispatcher에서 실행)
                withContext(Dispatchers.Main) {
                    reduce {
                        state.copy(
                            userDataList = userDataList,
                            diaryDataList = diaryDataList

                        )
                    }
                }
            } catch (e: Exception) {
                postSideEffect(DiarySideEffect.Toast("데이터 로드 에러"))
            }
        }
    }

    fun onDiaryClick(diaryData : Diary) = intent {
        if(diaryData.title == "") {
            val writeDiaryData = Diary(date = "", title = "", contents = "", mood = "")
            reduce {
                state.copy(
                    writeDiaryData = writeDiaryData
                )
            }
            postSideEffect(DiarySideEffect.NavigateToDiaryWriteScreen)
        } else {
            reduce {
                state.copy(clickDiaryData = diaryData)
            }
        }
    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                clickDiaryData = null
            )
        }
    }

}




@Immutable
data class DiaryState(
    val userDataList: List<User> = emptyList(),
    val diaryDataList: List<Diary> = emptyList(),
    val clickDiaryData: Diary? = null,
    val writeDiaryData: Diary? = null
)


//상태와 관련없는 것
sealed interface DiarySideEffect{
    class Toast(val message:String): DiarySideEffect
    data object NavigateToDiaryWriteScreen: DiarySideEffect

}