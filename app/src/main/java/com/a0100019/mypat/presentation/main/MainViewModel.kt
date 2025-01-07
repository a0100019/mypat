package com.a0100019.mypat.presentation.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.pet.PatDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
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
class MainViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao

) : ViewModel(), ContainerHost<MainState, MainSideEffect> {

    override val container: Container<MainState, MainSideEffect> = container(
        initialState = MainState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(MainSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
    }

    //room에서 데이터 가져옴
    fun loadData() = intent {

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
            val patWorldDataList = worldDao.getWorldDataListByType(type = "pat") ?: emptyList()
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
            val itemWorldDataList = worldDao.getWorldDataListByType(type = "item") // 여러 데이터를 가져옴
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


    fun onWorldChangeClick() = intent {
        reduce {
            state.copy(worldChange = !state.worldChange) // true/false 토글
        }
    }

    fun dialogPatIdChange(clickId : String) = intent {
        reduce {
            state.copy(dialogPatId = clickId)
        }
    }

    fun onWorldSelectClick() = intent {
        state.itemDataList.forEach { item ->
            itemDao.update(item)
        }
        state.patDataList.forEach { pat ->
            patDao.update(pat)
        }
        state.itemWorldDataList.forEach { world ->
            worldDao.update(world)
        }
        state.patWorldDataList.forEach { world ->
            worldDao.update(world)
        }
    }

    fun onWorldAddClick() = intent {

    }

    fun patWorldDataDelete(patId: String) = intent {
        val updatedPatWorldDataList = state.patWorldDataList.toMutableList()
        val index = updatedPatWorldDataList.indexOfFirst { it.value == patId }
        updatedPatWorldDataList[index] = updatedPatWorldDataList[index].copy(value = "0")

        val updatedPatDataList = state.patDataList.filterNot { it.id.toString() == patId } // patId와 일치하는 데이터 삭제

        reduce {
            state.copy(
                patWorldDataList = updatedPatWorldDataList,
                patDataList = updatedPatDataList
            )
        }
    }



    fun onPatSizeUpClick() = intent {
        val targetPat = state.patDataList.find { it.id.toString() == state.dialogPatId }!!
        val maxSize = targetPat.minFloat * 4 // 최대 크기 계산
        val updatedSize = (targetPat.sizeFloat + 0.1f).coerceAtMost(maxSize) // 크기를 제한

        val updatedPat = targetPat.copy(sizeFloat = updatedSize)
        val updatedPatDataList = state.patDataList.toMutableList().apply {
            set(indexOf(targetPat), updatedPat)
        }

        reduce {
            state.copy(patDataList = updatedPatDataList)
        }
    }

    fun onPatSizeDownClick() =  intent {
            val targetPat = state.patDataList.find { it.id.toString() == state.dialogPatId }!!
            val minSize = targetPat.minFloat // 최소 크기
            val updatedSize = (targetPat.sizeFloat - 0.1f).coerceAtLeast(minSize) // 크기를 제한

            val updatedPat = targetPat.copy(sizeFloat = updatedSize)
            val updatedPatDataList = state.patDataList.toMutableList().apply {
                set(indexOf(targetPat), updatedPat)
            }

            reduce {
                state.copy(patDataList = updatedPatDataList)
            }
        }




    fun onFirstGameClick() = intent {
        postSideEffect(MainSideEffect.FirstGameActivity)
    }

    fun onSecondGameClick() = intent {
        postSideEffect(MainSideEffect.SecondGameActivity)
    }

    fun onThirdGameClick() = intent {
        postSideEffect(MainSideEffect.ThirdGameActivity)
    }



}

@Immutable
data class MainState(
    val userData: List<User> = emptyList(),
    val worldChange: Boolean = false,
    val worldData: List<World> = emptyList(),
    val mapData: World? = null,
    val patDataList: List<Pat> = emptyList(),
    val patWorldDataList: List<World> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val itemWorldDataList: List<World> = emptyList(),
    val dialogPatId : String = "0",
    val showWorldAddDialog: Boolean = false,

)

//상태와 관련없는 것
sealed interface MainSideEffect{
    class Toast(val message:String): MainSideEffect
    data object FirstGameActivity: MainSideEffect
    data object SecondGameActivity: MainSideEffect
    data object ThirdGameActivity: MainSideEffect
}