package com.a0100019.mypat.presentation.store

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
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class StoreViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao

) : ViewModel(), ContainerHost<StoreState, StoreSideEffect> {

    override val container: Container<StoreState, StoreSideEffect> = container(
        initialState = StoreState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(StoreSideEffect.Toast(message = throwable.message.orEmpty()))
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

                // 모든 오픈 된 데이터 가져오기
                val allClosePatDataList = patDao.getAllClosePatData()
                val allCloseItemDataList = itemDao.getAllCloseItemData()
                val allCloseMapDataList = itemDao.getAllCloseMapData()

                // 펫 월드 데이터 리스트 가져오기
                val patWorldDataList = worldDao.getWorldDataListByType(type = "pat")

                // 아이템 월드 데이터 리스트 가져오기
                val itemWorldDataList = worldDao.getWorldDataListByType(type = "item")

                // 유저 데이터 가져오기
                val userDataList = userDao.getAllUserData()

                // UI 상태 업데이트 (Main Dispatcher에서 실행)
                withContext(Dispatchers.Main) {
                    reduce {
                        state.copy(
                            allCloseMapDataList = allCloseMapDataList,
                            allClosePatDataList = allClosePatDataList,
                            allCloseItemDataList = allCloseItemDataList,
                            patWorldDataList = patWorldDataList,
                            itemWorldDataList = itemWorldDataList,
                            userData = userDataList,
                        )
                    }
                }
            } catch (e: Exception) {
                postSideEffect(StoreSideEffect.Toast("데이터 로드 에러"))
            }
        }

    }

    fun onSimpleDialog(string : String) = intent {
        reduce {
            state.copy(
                simpleDialogState = string
            )
        }
    }

    fun onPatRandomClick() = intent {
        val moneyField = state.userData.find { it.id == "money" }

        if(moneyField!!.value.toInt() >= 100){
            moneyField.value = (moneyField.value.toInt() - 100).toString()

            val randomPat = state.allClosePatDataList.random() // 랜덤 객체 선택
            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // 현재 날짜 가져오기
            randomPat.date = currentDate // 날짜 업데이트

            userDao.update(id = moneyField.id, value = moneyField.value)
            patDao.update(randomPat)
            reduce {
                state.copy(
                    newPat = randomPat
                )
            }
            loadData()
        } else {
            //postSideEffect를 해야 인텐트 속에서도 잘 실행됨.
            postSideEffect(StoreSideEffect.Toast("돈이 부족합니다!"))
        }
    }

    fun onDialogCloseClick() = intent {
        reduce {
            state.copy(
                newPat = null,
                newMap = null,
                newItem = null,
                newName = "",
                showDialog = ""
            )
        }
    }

    fun onPatRoomUpClick() = intent {
        val cashField = state.userData.find { it.id == "cash" }
        val patRoomField = state.userData.find { it.id == "pat" }
        val firstField = state.patWorldDataList.find { it.open == "0" }

        if(cashField!!.value.toInt() >= 10) {
            if(patRoomField!!.value.toInt() > patRoomField.value2.toInt()) {
                patRoomField.value2 = (patRoomField.value2.toInt() + 1).toString()
                cashField.value = (cashField.value.toInt() - 10).toString()
                firstField!!.open = "1"

                userDao.update(id = patRoomField.id, value2 = patRoomField.value2)
                userDao.update(id = cashField.id, value = cashField.value)
                worldDao.update(firstField)
                reduce {
                    state.copy(
                        showDialog = "pat"
                    )
                }
                loadData()
            } else {
                postSideEffect(StoreSideEffect.Toast("더 이상 늘릴 수 없습니다!"))
            }

        } else {
            postSideEffect(StoreSideEffect.Toast("돈이 부족합니다!"))
        }

    }

    fun onItemRoomUpClick() = intent {
        val cashField = state.userData.find { it.id == "cash" }
        val itemRoomField = state.userData.find { it.id == "item" }
        val firstField = state.itemWorldDataList.find { it.open == "0" }

        if(cashField!!.value.toInt() >= 10) {
            if(itemRoomField!!.value.toInt() > itemRoomField.value2.toInt()) {
                itemRoomField.value2 = (itemRoomField.value2.toInt() + 1).toString()
                cashField.value = (cashField.value.toInt() - 10).toString()
                firstField!!.open = "1"

                userDao.update(id = itemRoomField.id, value2 = itemRoomField.value2)
                userDao.update(id = cashField.id, value = cashField.value)
                worldDao.update(firstField)
                reduce {
                    state.copy(
                        showDialog = "item"
                    )
                }
                loadData()
            } else {
                postSideEffect(StoreSideEffect.Toast("더 이상 늘릴 수 없습니다!"))
            }

        } else {
            postSideEffect(StoreSideEffect.Toast("돈이 부족합니다!"))
        }

    }

    fun onNameChangeClick(string: String) = intent {

    }

    fun changeShowDialog(string: String) = intent {
        reduce {
            state.copy(
                showDialog = string
            )
        }
    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onNameTextChange(nameText: String) = blockingIntent {
        reduce {
            state.copy(newName = nameText)
        }
    }



}



@Immutable
data class StoreState(
    val newPat: Pat? = null,
    val newItem: Item? = null,
    val newMap: Item? = null,
    val showDialog: String = "",
    val simpleDialogState: String = "",
    val newName: String = "",

    val userData: List<User> = emptyList(),
    val allClosePatDataList: List<Pat> = emptyList(),
    val allCloseItemDataList: List<Item> = emptyList(),
    val allCloseMapDataList: List<Item> = emptyList(),
    val patWorldDataList: List<World> = emptyList(),
    val itemWorldDataList: List<World> = emptyList(),
)


//상태와 관련없는 것
sealed interface StoreSideEffect{
    class Toast(val message:String): StoreSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}