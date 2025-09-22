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

        // 2. Flow인 일기 데이터는 collect로 가져와야 실시간 반영됨
        viewModelScope.launch {
            diaryDao.getAllFlowDiaryData().collect { diaryList ->
                reduce {
                    state.copy(
                        userDataList = userDataList,
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

        reduce {
            state.copy(
                writePossible = false,
                isError = false
            )
        }

        if(diaryData.state == "대기") {
            val writeDiaryData = Diary(id = diaryData.id, date = diaryData.date, state = "완료", contents = "", emotion = "emotion/smile.png")
            reduce {
                state.copy(
                    writeDiaryData = writeDiaryData,
                    firstWrite = true
                )
            }
            postSideEffect(DiarySideEffect.NavigateToDiaryWriteScreen)
        } else {
            reduce {
                state.copy(clickDiaryData = diaryData)
            }
        }

    }

    fun onDiaryChangeClick() = intent {
        reduce {
            state.copy(
                writeDiaryData = state.clickDiaryData!!,
                writePossible = true,
                firstWrite = false
            )
        }
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

    fun onDiaryFinishClick() = intent {
        println("내용 길이: ${state.writeDiaryData.contents.length}")

        if(state.writeDiaryData.contents.length > 9){

            //보상
            if(state.firstWrite){
                userDao.update(
                    id = "money",
                    value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + 1).toString()
                )
                postSideEffect(DiarySideEffect.Toast("보상을 획득했습니다"))
            }

            diaryDao.update(state.writeDiaryData)
            reduce {
                state.copy(
                    writeFinish = true
                )
            }

        } else {

//            postSideEffect(DiarySideEffect.Toast("10자 이상 작성해주세요"))

            reduce {
                state.copy(
                    isError = true
                )
            }

        }
    }

    fun onLastFinishClick() = intent {
        reduce {
            state.copy(
                clickDiaryData = null,
                writeFinish = false,
                dialogState = ""
            )
        }
        loadData()
    }

    @OptIn(OrbitExperimental::class)
    fun onContentsTextChange(contentsText: String) = blockingIntent {

        reduce {
            state.copy(writeDiaryData = state.writeDiaryData.copy(contents = contentsText))
        }

        if (contentsText.length > 9) {
            reduce {
                state.copy(
                    writePossible = true,
                    isError = false
                )
            }
        } else {
            reduce {
                state.copy(
                    writePossible = false,
                    isError = false
                )
            }
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

    fun emotionChangeClick(emotion: String) = intent {
        val newWriteDiaryData = state.writeDiaryData
        newWriteDiaryData.emotion = emotion
        reduce {
            state.copy(
                writeDiaryData = newWriteDiaryData,
                dialogState = ""
            )
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