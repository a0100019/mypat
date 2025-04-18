package com.a0100019.mypat.presentation.main


import android.util.Log
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
                postSideEffect(MainSideEffect.Toast("데이터 로드 에러"))
            }

        }
    }

    fun dialogPatIdChange(clickId : String) = intent {
        userDao.update(id = "selectPat", value = clickId)
        reduce {
            state.copy(dialogPatId = clickId)
        }
    }

    private fun worldDataDelete(id: String, type: String) = intent {
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

}

@Immutable
data class MainState(
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

    )

//상태와 관련없는 것
sealed interface MainSideEffect{
    class Toast(val message:String): MainSideEffect
}