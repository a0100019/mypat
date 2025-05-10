package com.a0100019.mypat.presentation.main.world

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.data.room.world.WorldDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
class WorldViewModel @Inject constructor(
    private val userDao: UserDao,
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

    fun loadData() = intent {
        // 병렬로 실행할 작업들을 viewModelScope.launch로 묶음
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 맵 데이터 가져오기
                val mapData = worldDao.getWorldDataById(1)

                //월드 데이터 가져오기
                val worldDataList = worldDao.getAllWorldData().drop(1)

                // 펫 flow 월드 데이터 리스트 가져오기
                val patFlowWorldDataList = worldDao.getFlowWorldDataListByType(type = "pat")
                    .map { list ->
                        list.map { patWorldData ->
                            patDao.getPatDataById(patWorldData.value)
                        }
                    }

                // 모든 오픈 된 데이터 가져오기
                val allPatDataList = patDao.getAllOpenPatData()
                val allItemDataList = itemDao.getAllOpenItemData()
                val allMapDataList = itemDao.getAllOpenMapData()

                val userFlowDataList = userDao.getAllUserDataFlow()
                val userDataList = userDao.getAllUserData()

                // UI 상태 업데이트 (Main Dispatcher에서 실행)
                withContext(Dispatchers.Main) {
                    reduce {
                        state.copy(
                            mapData = mapData,
                            patDataList = allPatDataList,
                            userFlowDataList = userFlowDataList,
                            itemDataList = allItemDataList,
                            allMapDataList = allMapDataList,
                            patFlowWorldDataList = patFlowWorldDataList,
                            worldDataList = worldDataList,
                            userDataList = userDataList
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("DataLoadError", "데이터 로드 중 에러 발생: ${e.message}", e)
                postSideEffect(WorldSideEffect.Toast("데이터 로드 에러"))
            }

        }
    }

    fun onAddDialogChangeClick() = intent {
        val newValue = when(state.addDialogChange) {
            "pat" -> "item"
            "item" -> "map"
            else -> "pat"
        }
        reduce {
            state.copy(addDialogChange = newValue) // true/false 토글
        }
    }

    fun onShowAddDialogClick() = intent {
        reduce {
            state.copy(showWorldAddDialog = !state.showWorldAddDialog) // true/false 토글
        }
    }

    fun dialogPatIdChange(clickId : String) = intent {
        userDao.update(id = "selectPat", value = clickId)
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
        worldDao.deleteAllExceptIdOne()
        state.worldDataList.forEach { world ->
            worldDao.insert(world)
        }
        state.mapData.let { worldDao.update(it) }
        userDao.updateUsers(state.userDataList)

        postSideEffect(WorldSideEffect.NavigateToMainScreen)

    }

    fun worldDataDelete(id: String, type: String) = intent {
        val currentList = state.worldDataList.toMutableList()
        Log.e("e", "targetIndex${id}}")
        val targetIndex = currentList.indexOfFirst { it.value == id && it.type == type }
        Log.e("e", "targetIndex${targetIndex}}")
        if (targetIndex == -1) return@intent // 못 찾았으면 종료

        // targetIndex에 있는 데이터 삭제
        currentList.removeAt(targetIndex)

        val newUserDataList = state.userDataList.toMutableList()
        if(type == "pat"){
            newUserDataList.find { it.id == "pat" }!!.value3 =
                ((newUserDataList.find { it.id == "pat" }!!.value3).toInt() - 1).toString()
        } else {
            newUserDataList.find { it.id == "item" }!!.value3 =
                ((newUserDataList.find { it.id == "item" }!!.value3).toInt() - 1).toString()
        }


        reduce {
            state.copy(
                worldDataList = currentList.toList(),
                userDataList =  newUserDataList
            )
        }

    }

    fun onAddPatClick(patId: String) = intent {

        // 1. patWorldDataList에서 patId와 일치하는 value 값을 찾는다
        val matchingIndex = state.worldDataList.indexOfFirst { it.value == patId && it.type == "pat" }

        if (matchingIndex != -1) {
            // 1.1 일치하는 데이터가 있는 경우 ( 펫이 월드에 나와 있는 경우 펫 제거)
            worldDataDelete(patId, "pat")
        } else {

            // 일치하는 데이터가 없어서 추가
            val currentUserList = state.userDataList.toMutableList()
            val userData = currentUserList.find { it.id == "pat" }

            //칸수 남아 있음
            if (userData!!.value2.toInt() > userData.value3.toInt()){
                val updatedList = state.worldDataList.toMutableList()

                val newWorld = World(
                    value = patId,
                    type = "pat"
                )

                updatedList.add(newWorld) // 맨 끝에 추가

                //user pat 업데이트, 사용 칸 수 +1
                val index = currentUserList.indexOf(userData)
                val updatedUserData = userData.copy(
                    value3 = (userData.value3.toInt() + 1).toString()
                )
                currentUserList[index] = updatedUserData

                reduce {
                    state.copy(
                        worldDataList = updatedList,
                        userDataList = currentUserList.toList() // 이쪽도 동일하게
                    )
                }

            } else {
                postSideEffect(WorldSideEffect.Toast("공간이 부족합니다!"))
            }

        }

    }

    fun onAddItemClick(itemId: String) = intent {

        // 1. patWorldDataList에서 patId와 일치하는 value 값을 찾는다
        val matchingIndex = state.worldDataList.indexOfFirst { it.value == itemId  && it.type == "item" }

        if (matchingIndex != -1) {
            // 1.1 일치하는 데이터가 있는 경우 ( 펫이 월드에 나와 있는 경우 펫 제거)
            worldDataDelete(itemId, "item")
        } else {
            // 일치하는 데이터가 없어서 추가
            val currentUserList = state.userDataList.toMutableList()
            val userData = currentUserList.find { it.id == "item" }

            //칸수 남아 있음
            if (userData!!.value2.toInt() > userData.value3.toInt()){
                val updatedList = state.worldDataList.toMutableList()

                val newWorld = World(
                    value = itemId,
                    type = "item"
                )

                updatedList.add(newWorld) // 맨 끝에 추가

                //user pat 업데이트
                val index = currentUserList.indexOf(userData)
                val updatedUserData = userData.copy(
                    value3 = (userData.value3.toInt() + 1).toString()
                )
                currentUserList[index] = updatedUserData

                // 상태 업데이트
                reduce {
                    state.copy(
                        worldDataList = updatedList,
                        userDataList = currentUserList
                    )
                }

            } else {
                postSideEffect(WorldSideEffect.Toast("공간이 부족합니다!"))
            }

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

    fun onPatDrag(patId: String, newX: Float, newY: Float) = intent {
        val targetPat = state.patDataList.find { it.id.toString() == patId }
        if (targetPat != null) {
            val updatedPat = targetPat.copy(x = newX, y = newY)
            val updatedPatDataList = state.patDataList.toMutableList().apply {
                set(indexOf(targetPat), updatedPat)
            }

            reduce {
                state.copy(patDataList = updatedPatDataList)
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


    fun onSelectMapImageClick(mapId: String) = intent {
        val newUrl = state.allMapDataList.find { it.id == mapId.toInt() }?.url ?: ""

        reduce {
            state.copy(
                mapData = state.mapData.copy(value = newUrl) // 기존 객체를 유지하면서 value만 변경
            )
        }
    }

}

@Immutable
data class WorldState(
    val userFlowDataList: Flow<List<User>> = flowOf(emptyList()),
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val allMapDataList: List<Item> = emptyList(),
    val patFlowWorldDataList: Flow<List<Pat>> = flowOf(emptyList()),
    val worldDataList: List<World> = emptyList(),
    val userDataList: List<User> = emptyList(),

    val mapData: World = World(),
    val dialogPatId: String = "0",
    val dialogItemId: String = "0",
    val showWorldAddDialog: Boolean = false,
    val addDialogChange: String = "pat",

    )

//상태와 관련없는 것
sealed interface WorldSideEffect{
    class Toast(val message:String): WorldSideEffect

    object NavigateToMainScreen : WorldSideEffect

}