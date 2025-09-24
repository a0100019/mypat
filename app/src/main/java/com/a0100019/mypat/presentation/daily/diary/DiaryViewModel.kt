package com.a0100019.mypat.presentation.daily.diary

import android.util.Log
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
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val userDao: UserDao,
    private val diaryDao: DiaryDao,
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

    init {
        loadData()
    }

    fun loadData() = intent {
        // 1. suspend로 바로 가져오는 유저 정보
        val userDataList = userDao.getAllUserData()
        val allDiaryData = diaryDao.getAllDiaryData()

        reduce {
            state.copy(
                userDataList = userDataList,
            )
        }

//        // 2. Flow인 일기 데이터는 collect로 가져와야 실시간 반영됨
        viewModelScope.launch {
            diaryDao.getAllFlowDiaryData().collect { diaryList ->
                reduce {
                    state.copy(
                        diaryDataList = diaryList,
                        diaryFilterDataList = diaryList,
                        dialogState = "",
                        clickDiaryData = null
                    )
                }
            }
        }
    }


    fun onDiaryClick(diaryData : Diary) = intent {

        if(diaryData.state == "대기") {
            userDao.update(id = "etc2", value = diaryData.date)
            postSideEffect(DiarySideEffect.NavigateToDiaryWriteScreen)
        } else {
            userDao.update(id = "etc2", value = diaryData.date)
            reduce {
                state.copy(
                    clickDiaryData = diaryData,
                )
            }
        }

    }

    fun onDiaryChangeClick() = intent {

        postSideEffect(DiarySideEffect.NavigateToDiaryWriteScreen)

    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                clickDiaryData = null,
                dialogState = "",
            )
        }
    }

    //검색
    fun onSearchClick() = intent {
        val newDiaryDataList = state.diaryDataList.filter { it.contents.contains(state.searchText) }
        if(state.emotionFilter != "") {
            newDiaryDataList.filter { it.emotion == state.emotionFilter }
        }
        reduce {
            state.copy(
                diaryFilterDataList = newDiaryDataList,
                dialogState = ""
            )
        }
    }

    fun onSearchClearClick() = intent {
        var newDiaryDataList = state.diaryDataList
        if(state.emotionFilter != "emotion/allEmotion.png") {
            newDiaryDataList = newDiaryDataList.filter { it.emotion == state.emotionFilter }
        }
        reduce {
            state.copy(
                diaryFilterDataList = newDiaryDataList,
                dialogState = "",
                searchText = ""
            )
        }
    }

    fun onDialogStateChange(string: String) = intent {
        reduce {
            state.copy(dialogState = string)
        }
    }


    fun onEmotionFilterClick(emotion: String) = intent {
        var newDiaryDataList = state.diaryDataList.filter { it.contents.contains(state.searchText) }
        if(emotion != "emotion/allEmotion.png") {
            newDiaryDataList = newDiaryDataList.filter { it.emotion == emotion }
        }
        reduce {
            state.copy(
                diaryFilterDataList = newDiaryDataList,
                dialogState = "",
                emotionFilter = emotion
            )
        }
    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onSearchTextChange(searchText: String) = blockingIntent {
        reduce {
            state.copy(searchText = searchText)
        }
    }

}

@Immutable
data class DiaryState(
    val userDataList: List<User> = emptyList(),
    val diaryDataList: List<Diary> = emptyList(),
    val diaryFilterDataList: List<Diary> = emptyList(),

    val clickDiaryData: Diary? = null,
    val writeDiaryData: Diary = Diary(date = "", contents = "", emotion = ""),
    val writePossible: Boolean = false,
    val isError: Boolean = false,
    val searchText: String = "",
    val dialogState: String = "",
    val emotionFilter: String = "emotion/allEmotion.png",
    val firstWrite: Boolean = true,
    val writeFinish: Boolean = false,
)

//상태와 관련없는 것
sealed interface DiarySideEffect{
    class Toast(val message:String): DiarySideEffect
    data object NavigateToDiaryWriteScreen: DiarySideEffect

}