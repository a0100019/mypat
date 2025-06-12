package com.a0100019.mypat.presentation.information

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.allUser.AllUserDao
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
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao,
    private val areaDao: AreaDao

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
        val areaData = worldDao.getWorldDataById(1)

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
        val allAreaDataList = areaDao.getAllAreaData()
        val allPatDataList = patDao.getAllPatData()
        val allItemDataList = itemDao.getAllItemData()
        val allUserDataList = allUserDao.getAllUserData()

        if(allUserDataList.size > 5) {

            // 높은 점수가 1등이라고 가정할 때
            val firstGameRank = allUserDataList
                .map { it.firstGame }        // 점수만 추출
                .sortedDescending()          // 높은 점수 순으로 정렬
                .indexOfFirst { it <= userDataList.find { it.id == "firstGame" }!!.value } + 1  // myScore보다 작거나 같은 첫 점수의 순위

            val secondGameRank = allUserDataList
                .map { it.secondGame }        // 점수만 추출
                .sortedDescending()          // 높은 점수 순으로 정렬
                .indexOfFirst { it <= userDataList.find { it.id == "secondGame" }!!.value } + 1  // myScore보다 작거나 같은 첫 점수의 순위

            val thirdGameEasyRank = allUserDataList
                .map { it.thirdGameEasy }        // 점수만 추출
                .sortedDescending()          // 높은 점수 순으로 정렬
                .indexOfFirst { it <= userDataList.find { it.id == "thirdGame" }!!.value } + 1  // myScore보다 작거나 같은 첫 점수의 순위

            val thirdGameNormalRank = allUserDataList
                .map { it.thirdGameNormal }        // 점수만 추출
                .sortedDescending()          // 높은 점수 순으로 정렬
                .indexOfFirst { it <= userDataList.find { it.id == "thirdGame" }!!.value2 } + 1  // myScore보다 작거나 같은 첫 점수의 순위

            val thirdGameHardRank = allUserDataList
                .map { it.thirdGameHard }        // 점수만 추출
                .sortedDescending()          // 높은 점수 순으로 정렬
                .indexOfFirst { it <= userDataList.find { it.id == "thirdGame" }!!.value3 } + 1  // myScore보다 작거나 같은 첫 점수의 순위

            reduce {
                state.copy(
                    gameRankList = listOf(
                        firstGameRank.toString(),
                        secondGameRank.toString(),
                        thirdGameEasyRank.toString(),
                        thirdGameNormalRank.toString(),
                        thirdGameHardRank.toString()
                    )
                )
            }

        }

        reduce {
            state.copy(
                areaData = areaData,
                patDataList = patDataList,
                itemDataList = itemDataList,
                userData = userDataList,
                allAreaDataList = allAreaDataList,
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
    val allAreaDataList: List<Area> = emptyList(),

    val gameRankList: List<String> = listOf("-", "-", "-", "-", "-"),

    val areaData: World? = null,

    )


//상태와 관련없는 것
sealed interface InformationSideEffect{
    class Toast(val message:String): InformationSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}