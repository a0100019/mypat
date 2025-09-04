package com.a0100019.mypat.presentation.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.world.WorldDao
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
class IndexViewModel @Inject constructor(
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val areaDao: AreaDao
) : ViewModel(), ContainerHost<IndexState, IndexSideEffect> {

    override val container: Container<IndexState, IndexSideEffect> = container(
        initialState = IndexState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(IndexSideEffect.Toast(message = throwable.message.orEmpty()))
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 맵 데이터 가져오기
                val allAreaDataList = areaDao.getAllAreaData()

                // 모든 펫 데이터 가져오기
                val allPatDataList = patDao.getAllPatData()

                val allItemDataList = itemDao.getAllItemData()

                // UI 상태 업데이트 (Main Dispatcher에서 실행)
                withContext(Dispatchers.Main) {
                    reduce {
                        state.copy(
                            allAreaDataList = allAreaDataList,
                            allPatDataList = allPatDataList,
                            allItemDataList = allItemDataList
                        )
                    }
                }
            } catch (e: Exception) {
                postSideEffect(IndexSideEffect.Toast("데이터 로드 에러"))
            }
        }
    }

    fun onTypeChangeClick(type: String) = intent {

        reduce {
            state.copy(
                typeChange = type,
                page = 1
            )
        }
    }

    fun onCardClick(index: Int) = intent {
        reduce {
            when (state.typeChange) {
                "pat" -> {
                    state.copy(dialogPatIndex = index)
                }
                "item" -> {
                    state.copy(dialogItemIndex = index)
                }
                else -> {
                    state.copy(dialogAreaIndex = index)
                }
            }
        }
    }

    fun onCloseDialog() = intent {
        reduce {
            state.copy(
                dialogPatIndex = -1,
                dialogItemIndex = -1,
                dialogAreaIndex = -1
            )
        }
    }

    fun onPageChangeClick(next: Boolean) = intent {

        val lastPage = when(state.typeChange) {
            "pat" -> state.allPatDataList.size/9 + 1
            "item" -> state.allItemDataList.size/9 + 1
            else -> state.allAreaDataList.size/9 + 1
        }

        if(next) {
            if(lastPage > state.page){
                reduce {
                    state.copy(page = state.page + 1)
                }
            }
        } else {
            if(state.page > 1){
                reduce {
                    state.copy(page = state.page - 1)
                }
            }
        }

    }

}

@Immutable
data class IndexState(
    val allPatDataList: List<Pat> = emptyList(),
    val allItemDataList: List<Item> = emptyList(),
    val allAreaDataList: List<Area> = emptyList(),

    val typeChange: String = "pat",
    val dialogPatIndex: Int = -1,
    val dialogItemIndex: Int = -1,
    val dialogAreaIndex: Int = -1,
    val page: Int = 1
)


//상태와 관련없는 것
sealed interface IndexSideEffect{
    class Toast(val message:String): IndexSideEffect
//    data object NavigateToDailyActivity: IndexSideEffect

}