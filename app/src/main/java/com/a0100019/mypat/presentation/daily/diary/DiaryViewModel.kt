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
                            diaryDataList = diaryDataList,
                            diaryFilterDataList = diaryDataList

                        )
                    }
                }
            } catch (e: Exception) {
                postSideEffect(DiarySideEffect.Toast("데이터 로드 에러"))
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
            val writeDiaryData = Diary(id = diaryData.id, date = diaryData.date, state = "완료", contents = "", emotion = "")
            reduce {
                state.copy(
                    writeDiaryData = writeDiaryData,
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
        if(state.writeDiaryData.contents.length > 9){

            //보상
            userDao.update(id = "money", value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + 100).toString())

            diaryDao.update(state.writeDiaryData)
            reduce {
                state.copy(
                    clickDiaryData = null,
                )
            }
            loadData()

        } else {

            reduce {
                state.copy(
                    isError = true
                )
            }

        }
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
        if(state.emotionFilter != "etc/snowball.png") {
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
        if(emotion != "etc/snowball.png") {
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
    val emotionFilter: String = "etc/snowball.png"
)

//상태와 관련없는 것
sealed interface DiarySideEffect{
    class Toast(val message:String): DiarySideEffect
    data object NavigateToDiaryWriteScreen: DiarySideEffect

}