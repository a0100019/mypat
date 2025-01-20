package com.a0100019.mypat.presentation.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.pet.PatDao
import com.a0100019.mypat.data.room.world.WorldDao
import com.a0100019.mypat.presentation.main.MainSideEffect
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
    private val itemDao: ItemDao
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
                val allMapDataList = itemDao.getAllMapData()

                // 모든 펫 데이터 가져오기
                val allPatDataList = patDao.getAllPatData()

                val allItemDataList = itemDao.getAllItemData()

                // UI 상태 업데이트 (Main Dispatcher에서 실행)
                withContext(Dispatchers.Main) {
                    reduce {
                        state.copy(
                            allMapDataList = allMapDataList,
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
            state.copy(typeChange = type)
        }
    }


}





@Immutable
data class IndexState(
    val allPatDataList: List<Pat> = emptyList(),
    val allItemDataList: List<Item> = emptyList(),
    val allMapDataList: List<Item> = emptyList(),

    val typeChange: String = "pat"
)


//상태와 관련없는 것
sealed interface IndexSideEffect{
    class Toast(val message:String): IndexSideEffect
//    data object NavigateToDailyActivity: IndexSideEffect

}