package com.a0100019.mypat.presentation.information

import androidx.lifecycle.ViewModel
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
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class InformationViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao

    ) : ViewModel(), ContainerHost<InformationState, InformationSideEffect> {

    override val container: Container<InformationState, InformationSideEffect> = container(
        initialState = InformationState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(InformationSideEffect.Toast(message = throwable.message.orEmpty()))
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

        // 맵 데이터 가져오기
        val mapData = worldDao.getWorldDataById(1)

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

        val userDataList = userDao.getAllUserData()
        val allMapDataList = itemDao.getAllMapData()
        val allPatDataList = patDao.getAllPatData()
        val allItemDataList = itemDao.getAllItemData()

        reduce {
            state.copy(
                mapData = mapData,
                patDataList = patDataList,
                itemDataList = itemDataList,
                userData = userDataList,
                allMapDataList = allMapDataList,
                allPatDataList = allPatDataList,
                allItemDataList = allItemDataList
            )
        }

    }

}




@Immutable
data class InformationState(
    val userData: List<User> = emptyList(),
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val allPatDataList: List<Pat> = emptyList(),
    val allItemDataList: List<Item> = emptyList(),
    val allMapDataList: List<Item> = emptyList(),

    val mapData: World? = null,

    )


//상태와 관련없는 것
sealed interface InformationSideEffect{
    class Toast(val message:String): InformationSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}