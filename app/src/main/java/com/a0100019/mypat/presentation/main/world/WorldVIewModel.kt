package com.a0100019.mypat.presentation.main.world

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.pet.PatDao
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.data.room.world.WorldDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class WorldViewModel @Inject constructor(
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao

) : ViewModel(), ContainerHost<WorldState, WorldSideEffect> {

    override val container: Container<WorldState, WorldSideEffect> = container(
        initialState = WorldState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(WorldSideEffect.Toast(message = throwable.message.orEmpty()))
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
        // 데이터 실시간으로 받아오려면 collect 해야함
        // 데이터 간의 의존관계가 있다면 collect문 안에 또 collect문 적기
        // viewModelScope.launch { } 로 감싸면 병렬로 동시 진행


        viewModelScope.launch {
            val mapData = worldDao.getWorldDataById("map")
            reduce {
                state.copy(mapData = mapData)
            }
        }

        viewModelScope.launch {
            // 첫 번째 데이터 가져오기
            val patWorldDataList = worldDao.getWorldDataListByTypeOpen(type = "pat", open = "1") ?: emptyList()
            reduce {
                state.copy(patWorldDataList = patWorldDataList)
            }

            // 두 번째 데이터 가져오기
            val patDataList = patWorldDataList.mapNotNull { patWorldData ->
                // 데이터가 없으면 null 반환
                patDao.getPatDataById(patWorldData.value)
            }
            reduce {
                state.copy(patDataList = patDataList)
            }
        }



        viewModelScope.launch {
            // 첫 번째 데이터 가져오기
            val itemWorldDataList = worldDao.getWorldDataListByTypeOpen(type = "item", open = "1") // 여러 데이터를 가져옴
            reduce {
                state.copy(itemWorldDataList = itemWorldDataList)
            }

            // 두 번째 데이터 가져오기
            val itemDataList = itemWorldDataList.mapNotNull { itemWorldData ->
                itemDao.getItemDataById(itemWorldData.value) // 각 value에 맞는 데이터 가져오기
            }
            reduce {
                state.copy(itemDataList = itemDataList)
            }
        }

    }

    fun dialogPatIdChange(clickId : String) = intent {
        reduce {
            state.copy(dialogPatId = clickId)
        }
    }

    fun onFirstGameClick() = intent {
        postSideEffect(WorldSideEffect.FirstGameActivity)
    }

    fun onSecondGameClick() = intent {
        postSideEffect(WorldSideEffect.SecondGameActivity)
    }

    fun onThirdGameClick() = intent {
        postSideEffect(WorldSideEffect.ThirdGameActivity)
    }




}

@Immutable
data class WorldState(
    val worldData: List<World> = emptyList(),
    val mapData: World? = null,
    val patDataList: List<Pat> = emptyList(),
    val patWorldDataList: List<World> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val itemWorldDataList: List<World> = emptyList(),
    val dialogPatId : String = "0"
    )


//상태와 관련없는 것
sealed interface WorldSideEffect{
    class Toast(val message:String): WorldSideEffect
    data object FirstGameActivity: WorldSideEffect
    data object SecondGameActivity: WorldSideEffect
    data object ThirdGameActivity: WorldSideEffect

}