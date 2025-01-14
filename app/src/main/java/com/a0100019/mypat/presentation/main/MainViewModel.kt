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

    fun loadData() = intent {
        // 병렬로 실행할 작업들을 viewModelScope.launch로 묶음
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 맵 데이터 가져오기
                val mapData = worldDao.getWorldDataById("map")

                // 펫 월드 데이터 리스트 가져오기
                val patWorldDataList = worldDao.getWorldDataListByType(type = "pat") ?: emptyList()
                val patDataList = patWorldDataList.mapNotNull { patWorldData ->
                    patDao.getPatDataById(patWorldData.value)
                }

                // 아이템 월드 데이터 리스트 가져오기
                val itemWorldDataList = worldDao.getWorldDataListByType(type = "item") ?: emptyList()
                val itemDataList = itemWorldDataList.mapNotNull { itemWorldData ->
                    itemDao.getItemDataById(itemWorldData.value)
                }

                // 모든 펫 데이터 가져오기
                val allPatDataList = patDao.getAllPatData()

                val userDataList = userDao.getAllUserData()

                // UI 상태 업데이트 (Main Dispatcher에서 실행)
                withContext(Dispatchers.Main) {
                    reduce {
                        state.copy(
                            mapData = mapData,
                            patWorldDataList = patWorldDataList,
                            patDataList = patDataList,
                            itemWorldDataList = itemWorldDataList,
                            itemDataList = itemDataList,
                            allPatDataList = allPatDataList,
                            userDataList = userDataList
                        )
                    }
                }
            } catch (e: Exception) {
                postSideEffect(MainSideEffect.Toast("데이터 로드 에러"))
            }
        }
    }



    fun onWorldChangeClick() = intent {
        reduce {
            state.copy(worldChange = !state.worldChange) // true/false 토글
        }
    }

    fun onShowAddDialogClick() = intent {
        reduce {
            state.copy(showWorldAddDialog = !state.showWorldAddDialog) // true/false 토글
        }
    }

    fun onShowUserInformationDialogClick() = intent {
        reduce {
            state.copy(showUserInformationDialog = !state.showUserInformationDialog) // true/false 토글
        }
    }

    fun dialogPatIdChange(clickId : String) = intent {
        reduce {
            state.copy(dialogPatId = clickId)
        }
    }

    fun dialogItemIdChange(clickId : String) = intent {
        reduce {
            state.copy(dialogItemId = clickId)
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
        loadData()
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

    fun itemWorldDataDelete(itemId: String) = intent {
        val updatedItemWorldDataList = state.itemWorldDataList.toMutableList()
        val index = updatedItemWorldDataList.indexOfFirst { it.value == itemId }
        updatedItemWorldDataList[index] = updatedItemWorldDataList[index].copy(value = "0")

        val updatedItemDataList = state.itemDataList.filterNot { it.id.toString() == itemId }

        reduce {
            state.copy(
                itemWorldDataList = updatedItemWorldDataList,
                itemDataList = updatedItemDataList
            )
        }
    }

    fun onItemDrag(itemId: String, newX: Float, newY: Float) = intent {
        val targetItem = state.itemDataList.find { it.id.toString() == itemId }
        if (targetItem != null) {
            val updatedItem = targetItem.copy(x = newX, y = newY)
            val updatedItemDataList = state.itemDataList.toMutableList().apply {
                set(indexOf(targetItem), updatedItem)
            }

            reduce {
                state.copy(itemDataList = updatedItemDataList)
            }
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

    fun onItemSizeUpClick() = intent {
        val targetItem = state.itemDataList.find { it.id.toString() == state.dialogItemId }!!
        val maxSize = targetItem.minFloat * 4 // 최대 크기 계산
        val updatedSize = (targetItem.sizeFloat + 0.1f).coerceAtMost(maxSize) // 크기를 제한

        val updatedItem = targetItem.copy(sizeFloat = updatedSize)
        val updatedItemDataList = state.itemDataList.toMutableList().apply {
            set(indexOf(targetItem), updatedItem)
        }

        reduce {
            state.copy(itemDataList = updatedItemDataList)
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

    fun onItemSizeDownClick() =  intent {
        val targetItem = state.itemDataList.find { it.id.toString() == state.dialogItemId }!!
        val minSize = targetItem.minFloat // 최소 크기
        val updatedSize = (targetItem.sizeFloat - 0.1f).coerceAtLeast(minSize) // 크기를 제한

        val updatedItem = targetItem.copy(sizeFloat = updatedSize)
        val updatedItemDataList = state.itemDataList.toMutableList().apply {
            set(indexOf(targetItem), updatedItem)
        }

        reduce {
            state.copy(itemDataList = updatedItemDataList)
        }
    }


    fun onAddPatImageClick(patId: String) = intent {
        // 1. patWorldDataList에서 patId와 일치하는 value 값을 찾는다
        val matchingIndex = state.patWorldDataList.indexOfFirst { it.value == patId }

        val updatedState = if (matchingIndex != -1) {
            // 1.1 일치하는 데이터가 있는 경우 ( 펫이 월드에 나와 있는 경우 펫 제거)
            val updatedPatWorldDataList = state.patWorldDataList.mapIndexed { index, world ->
                if (index == matchingIndex) world.copy(value = "0") else world
            }

            val updatedPatDataList = state.patDataList.filter { it.id != patId.toInt() }

            // 새로운 상태 생성
            state.copy(
                patWorldDataList = updatedPatWorldDataList,
                patDataList = updatedPatDataList
            )
        } else {
            // 1.2 일치하는 데이터가 없는 경우 ( 월드에 펫이 없을 때 추가 )
            val zeroIndex = state.patWorldDataList.indexOfFirst { it.value == "0" }
            val openCount = state.patWorldDataList.count { it.open == "1" }

            if (zeroIndex != -1 && zeroIndex < openCount) {
                val updatedPatWorldDataList = state.patWorldDataList.mapIndexed { index, world ->
                    if (index == zeroIndex) world.copy(value = patId) else world
                }

                val newPatData = state.allPatDataList.find { it.id == patId.toInt() }
                val updatedPatDataList = if (newPatData != null) {
                    state.patDataList + newPatData
                } else {
                    state.patDataList
                }

                // 새로운 상태 생성
                state.copy(
                    patWorldDataList = updatedPatWorldDataList,
                    patDataList = updatedPatDataList
                )
            } else {
                // "0"인 데이터가 없는 경우의 처리
                println("No available slot in patWorldDataList to update with patId")
                postSideEffect(MainSideEffect.Toast("공간이 부족합니다!"))
                state // 상태 변경 없음
            }
        }

        // 상태 업데이트
        reduce { updatedState }
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
    val userDataList: List<User> = emptyList(),
    val worldChange: Boolean = false,
    val worldData: List<World> = emptyList(),
    val mapData: World? = null,
    val patDataList: List<Pat> = emptyList(),
    val patWorldDataList: List<World> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val itemWorldDataList: List<World> = emptyList(),
    val dialogPatId : String = "0",
    val dialogItemId : String = "0",
    val showWorldAddDialog: Boolean = false,
    val allPatDataList: List<Pat> = emptyList(),
    val showUserInformationDialog: Boolean = false

)

//상태와 관련없는 것
sealed interface MainSideEffect{
    class Toast(val message:String): MainSideEffect
    data object FirstGameActivity: MainSideEffect
    data object SecondGameActivity: MainSideEffect
    data object ThirdGameActivity: MainSideEffect
}