package com.a0100019.mypat.presentation.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
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
    private val itemDao: ItemDao,
    private val areaDao: AreaDao

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
                val allCloseMapDataList = areaDao.getAllCloseMapData()

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
                            allCloseAreaDataList = allCloseMapDataList,
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

    fun onDialogCloseClick() = intent {
        reduce {
            state.copy(
                newPat = null,
                newMap = null,
                newItem = null,
                newName = "",
                showDialog = "",
                simpleDialogState = "",
                selectPatData = null,
                selectItemData = null,
            )
        }
    }

    fun onPatRoomUpClick() = intent {
        val moneyField = state.userData.find { it.id == "money" }
        val patRoomField = state.userData.find { it.id == "pat" }
        val emptyRoom = patRoomField?.value2!!.toInt() > patRoomField.value3.toInt()

        //돈 있는지
        if(moneyField!!.value.toInt() >= 10) {

            //빈방 있는지
            if(emptyRoom) {

                userDao.update(id = "pat", value3 = (patRoomField.value3.toInt() + 1).toString())
                userDao.update(id = moneyField.id, value = (moneyField.value.toInt() - 10).toString())
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
        val moneyField = state.userData.find { it.id == "money" }
        val itemRoomField = state.userData.find { it.id == "item" }
        val emptyRoom = itemRoomField?.value2!!.toInt() > itemRoomField.value3.toInt()

        //돈 있는지
        if(moneyField!!.value.toInt() >= 10) {

            //빈방 있는지
            if(emptyRoom) {

                userDao.update(id = "item", value3 = (itemRoomField.value3.toInt() + 1).toString())
                userDao.update(id = moneyField.id, value = (moneyField.value.toInt() - 10).toString())
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

    fun onNameChangeConfirm() = intent {
        // 조건 추가
        if(true) {
            reduce {
                state.copy(
                    simpleDialogState = "가능한 닉네임입니다 변경하겠습니까?"
                )
            }
        } else {
            postSideEffect(StoreSideEffect.Toast("이미 존재하는 닉네임입니다."))
        }
    }

    fun onNameChangeClick() = intent {

        val moneyField = state.userData.find { it.id == "money" }

        if(moneyField!!.value.toInt() >= 1) {

            moneyField.value = (moneyField.value.toInt() - 1).toString()
            userDao.update(id = moneyField.id, value = moneyField.value)

            userDao.update("name", value = state.newName)
            postSideEffect(StoreSideEffect.Toast("닉네임이 변경되었습니다."))
            loadData()
            onDialogCloseClick()

        } else {
            postSideEffect(StoreSideEffect.Toast("돈이 부족합니다!"))
        }

    }

    fun changeShowDialog(string: String) = intent {
        reduce {
            state.copy(
                showDialog = string
            )
        }
    }

    fun onMoneyChangeClick() = intent {
        val moneyField = state.userData.find { it.id == "money" }

        if(moneyField!!.value2.toInt() >= 1) {

            moneyField.value2 = (moneyField.value2.toInt() - 1).toString()
            userDao.update(id = moneyField.id, value2 = moneyField.value2)
            moneyField.value = (moneyField.value.toInt() + 100).toString()
            userDao.update(id = moneyField.id, value = moneyField.value)

            postSideEffect(StoreSideEffect.Toast("교환 완료."))
            loadData()
            onDialogCloseClick()

        } else {
            postSideEffect(StoreSideEffect.Toast("cash가 부족합니다!"))
        }
    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onNameTextChange(nameText: String) = blockingIntent {
        reduce {
            state.copy(newName = nameText)
        }
    }


    fun onItemClick(index: Int) = intent {
        reduce {
            state.copy(
                selectItemData = state.itemStoreDataList[index]
            )
        }
    }

    fun onItemSelectClick() = intent {
        val selectItem = state.selectItemData
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // 현재 날짜 가져오기
        selectItem?.date = currentDate // 날짜 업데이트

        selectItem?.let { itemDao.update(it) }
        reduce {
            state.copy(
                newItem = selectItem
            )
        }
        loadData()
    }

    fun onItemSelectCloseClick() = intent {
        reduce {
            state.copy(
                selectItemData = null
            )
        }
    }

    fun onItemStoreClick() = intent {
        val moneyField = state.userData.find { it.id == "money" }

        if(moneyField!!.value2.toInt() >= 1){
            moneyField.value2 = (moneyField.value2.toInt() - 1).toString()

            val randomItemList = state.allCloseItemDataList
                .shuffled()
                .take(5)
                .toMutableList()

            if(randomItemList.size == 0) {
                postSideEffect(StoreSideEffect.Toast("모든 아이템을 모두 얻었습니다!"))
                return@intent
            }
            // 부족한 경우 기본 객체 추가 (예: 빈 Pat 객체)
            while (randomItemList.size < 5) {
                randomItemList.add(Item(url = "")) // Pat.default()는 적절한 기본 객체로 변경
            }

            userDao.update(id = moneyField.id, value2 = moneyField.value2)
            reduce {
                state.copy(
                    itemStoreDataList = randomItemList,
                    showDialog = "itemStore"
                )
            }
        } else {
            //postSideEffect를 해야 인텐트 속에서도 잘 실행됨.
            postSideEffect(StoreSideEffect.Toast("돈이 부족합니다!"))
        }
    }

    fun onPatStoreClick() = intent {
        val moneyField = state.userData.find { it.id == "money" }

        if(moneyField!!.value2.toInt() >= 1){
            moneyField.value2 = (moneyField.value2.toInt() - 1).toString()

            val randomPatList = state.allClosePatDataList
                .shuffled()
                .take(5)
                .toMutableList()
            // 부족한 경우 기본 객체 추가 (예: 빈 Pat 객체)
            while (randomPatList.size < 5) {
                randomPatList.add(Pat(url = "")) // Pat.default()는 적절한 기본 객체로 변경
            }

            // 각 요소를 두 번씩 추가
            val patEggDataList = (randomPatList + randomPatList).shuffled()

            userDao.update(id = moneyField.id, value2 = moneyField.value2)
            reduce {
                state.copy(
                    patStoreDataList = randomPatList,
                    patEggDataList = patEggDataList,
                    showDialog = "patStore"
                )
            }
        } else {
            //postSideEffect를 해야 인텐트 속에서도 잘 실행됨.
            postSideEffect(StoreSideEffect.Toast("돈이 부족합니다!"))
        }
    }

    fun onPatSelectClick() = intent {

        val selectPat = state.selectPatData
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // 현재 날짜 가져오기
        selectPat?.date = currentDate // 날짜 업데이트

        selectPat?.let { patDao.update(it) }
        reduce {
            state.copy(
                newPat = selectPat
            )
        }
        loadData()

    }

    fun onPatEggClick(index: Int) = intent {
        val patEggDataList = state.patEggDataList
        val patSelectDataList = state.patSelectDataList.toMutableList()

        // 선택한 데이터를 patSelectDataList로 복사
        val selectedItem = patEggDataList[index]
        patSelectDataList.add(selectedItem)

        val newIndexList = state.patSelectIndexList + index

        // 상태 업데이트
        reduce {
            state.copy(
                patSelectDataList = patSelectDataList,
                patSelectIndexList = newIndexList
            )
        }

        val idCounts = patSelectDataList
            .filter { it.id != 0 } // id가 0이 아닌 데이터 필터링
            .groupingBy { it.id }
            .eachCount() // id별 개수 계산

        val result = patSelectDataList.filter { it.id != 0 && (idCounts[it.id] ?: 0) >= 2 }

        reduce {
            state.copy(
                selectPatData = result.firstOrNull() // result가 비어 있으면 기존 값 유지
            )
        }



    }

    fun onPatAdvertisementClick() = intent {

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
    val selectPatData: Pat? = null,
    val selectItemData: Item? = null,

    val userData: List<User> = emptyList(),
    val allClosePatDataList: List<Pat> = emptyList(),
    val allCloseItemDataList: List<Item> = emptyList(),
    val allCloseAreaDataList: List<Area> = emptyList(),
    val patWorldDataList: List<World> = emptyList(),
    val itemWorldDataList: List<World> = emptyList(),
    val patStoreDataList: List<Pat> = emptyList(),
    val patEggDataList: List<Pat> = emptyList(),
    val patSelectDataList: List<Pat> = emptyList(),
    val patSelectIndexList: List<Int> = emptyList(),
    val itemStoreDataList: List<Item> = emptyList()


    )


//상태와 관련없는 것
sealed interface StoreSideEffect{
    class Toast(val message:String): StoreSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}