package com.a0100019.mypat.presentation.daily.diary

import android.util.Log
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
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
class DiaryWriteViewModel @Inject constructor(
    private val userDao: UserDao,
    private val diaryDao: DiaryDao,
) : ViewModel(), ContainerHost<DiaryWriteState, DiaryWriteSideEffect> {

    override val container: Container<DiaryWriteState, DiaryWriteSideEffect> = container(
        initialState = DiaryWriteState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(DiaryWriteSideEffect.Toast(message = throwable.message.orEmpty()))
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
        val userDataEtc2Value = userDao.getValueById("etc2")
        val allDiaryData = diaryDao.getAllDiaryData()

        if(!userDataEtc2Value.startsWith("0")){
            //0있으면 하루 미션 아닌 일기
            val clickDiaryData = allDiaryData.find { it.date == userDataEtc2Value }
            if (clickDiaryData!!.state == "대기") {
                reduce {
                    state.copy(
                        firstWrite = true,
                        writeDiaryData = Diary(
                            id = clickDiaryData.id,
                            date = clickDiaryData.date,
                            state = "완료",
                            contents = "",
                            emotion = "emotion/smile.png"
                        )
                    )
                }
            } else {
                reduce {
                    state.copy(
                        firstWrite = false,
                        writeDiaryData = clickDiaryData
                    )
                }
            }
        } else {
            //하루미션 아닌 일기

            val lastId = allDiaryData.maxOfOrNull { it.id } ?: 0   // 리스트 비어도 안전

            val newId = if (lastId < 10000) 10000 else lastId + 1

            reduce {
                state.copy(
                    firstWrite = true,
                    writeDiaryData = Diary(
                        id = newId,
                        date = userDataEtc2Value.drop(1),
                        state = "완료",
                        contents = "",
                        emotion = "emotion/smile.png"
                    )
                )
            }
        }

        reduce {
            state.copy(
                dialogState = "",
                writeFinish = false,
                userDataList = userDataList
            )
        }

    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                dialogState = "",
            )
        }
    }

    fun onDiaryFinishClick() = intent {
        println("내용 길이: ${state.writeDiaryData.contents.length}")

        if(state.writeDiaryData.contents.length > 1){

            //보상
            if(state.firstWrite && state.writeDiaryData.id < 10000){
                Log.e("DiaryViewModel", state.userDataList.find { it.id == "money" }!!.value)
                userDao.update(
                    id = "money",
                    value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + 1).toString()
                )
                postSideEffect(DiaryWriteSideEffect.Toast("보상을 획득했습니다"))
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
            postSideEffect(DiaryWriteSideEffect.Toast("10자 이상 입력해주세요"))
            reduce {
                state.copy(
                    writePossible = false,
                    isError = false
                )
            }
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

}

@Immutable
data class DiaryWriteState(
    val userDataList: List<User> = emptyList(),
    val diaryDataList: List<Diary> = emptyList(),
    val diaryFilterDataList: List<Diary> = emptyList(),

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
sealed interface DiaryWriteSideEffect{
    class Toast(val message:String): DiaryWriteSideEffect

}