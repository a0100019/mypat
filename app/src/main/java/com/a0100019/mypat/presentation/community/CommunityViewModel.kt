package com.a0100019.mypat.presentation.community

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
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
class CommunityViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val allUserDao: AllUserDao
) : ViewModel(), ContainerHost<CommunityState, CommunitySideEffect> {

    override val container: Container<CommunityState, CommunitySideEffect> = container(
        initialState = CommunityState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(CommunitySideEffect.Toast(message = throwable.message.orEmpty()))
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
        val userDataList = userDao.getAllUserData()
        val patDataList = patDao.getAllPatData()
        val itemDataList = itemDao.getAllItemData()
        val allUserDataList = allUserDao.getAllUserData()

        val page = userDataList.find { it.id == "etc" }!!.value.toInt()
        val allUserData1 = allUserDataList[4*page]
        val allUserData2 = allUserDataList[4*page + 1]
        val allUserData3 = allUserDataList[4*page + 2]
        val allUserData4 = allUserDataList[4*page + 3]
        val allUserWorldDataList1: List<String> = allUserData1.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
        val allUserWorldDataList2: List<String> = allUserData2.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
        val allUserWorldDataList3: List<String> = allUserData3.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
        val allUserWorldDataList4: List<String> = allUserData4.worldData
            .split("/")
            .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거

        reduce {
            state.copy(
                userDataList = userDataList,
                patDataList = patDataList,
                itemDataList = itemDataList,
                allUserDataList =  allUserDataList,
                page = page,
                allUserData1 = allUserData1,
                allUserData2 = allUserData2,
                allUserData3 = allUserData3,
                allUserData4 = allUserData4,
                allUserWorldDataList1 = allUserWorldDataList1,
                allUserWorldDataList2 = allUserWorldDataList2,
                allUserWorldDataList3 = allUserWorldDataList3,
                allUserWorldDataList4 = allUserWorldDataList4
            )
        }
    }


    fun opPageUpClick() = intent {

        val page = state.page
        val allUserDataList = state.allUserDataList

        if (allUserDataList.size > page * 4 + 8) {
            //다음 페이지
            val allUserData1 = allUserDataList[4*page + 4]
            val allUserData2 = allUserDataList[4*page + 5]
            val allUserData3 = allUserDataList[4*page + 6]
            val allUserData4 = allUserDataList[4*page + 7]
            val allUserWorldDataList1: List<String> = allUserData1.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList2: List<String> = allUserData2.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList3: List<String> = allUserData3.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList4: List<String> = allUserData4.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거

            userDao.update(id = "etc", value = (page+1).toString())
            reduce {
                state.copy(
                    page = page + 1,
                    allUserData1 = allUserData1,
                    allUserData2 = allUserData2,
                    allUserData3 = allUserData3,
                    allUserData4 = allUserData4,
                    allUserWorldDataList1 = allUserWorldDataList1,
                    allUserWorldDataList2 = allUserWorldDataList2,
                    allUserWorldDataList3 = allUserWorldDataList3,
                    allUserWorldDataList4 = allUserWorldDataList4
                )
            }

        } else {
            //첫 페이지

            val allUserData1 = allUserDataList[4*page]
            val allUserData2 = allUserDataList[4*page + 1]
            val allUserData3 = allUserDataList[4*page + 2]
            val allUserData4 = allUserDataList[4*page + 3]
            val allUserWorldDataList1: List<String> = allUserData1.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList2: List<String> = allUserData2.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList3: List<String> = allUserData3.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거
            val allUserWorldDataList4: List<String> = allUserData4.worldData
                .split("/")
                .filter { it.isNotBlank() } // 혹시 모를 빈 문자열 제거

            userDao.update(id = "etc", value = "0")
            reduce {
                state.copy(
                    page = 0,
                    allUserData1 = allUserData1,
                    allUserData2 = allUserData2,
                    allUserData3 = allUserData3,
                    allUserData4 = allUserData4,
                    allUserWorldDataList1 = allUserWorldDataList1,
                    allUserWorldDataList2 = allUserWorldDataList2,
                    allUserWorldDataList3 = allUserWorldDataList3,
                    allUserWorldDataList4 = allUserWorldDataList4
                )
            }
        }

    }

    fun onSituationChange(newSituation: String) = intent {
        reduce {
            state.copy(
                situation = newSituation
            )
        }
    }

    fun onUserClick(clickUserNumber: Int) = intent {
        val selectedUser = when (clickUserNumber) {
            1 -> state.allUserData1
            2 -> state.allUserData2
            3 -> state.allUserData3
            4 -> state.allUserData4
            else -> AllUser()
        }
        val selectedUserWorldDataList = when (clickUserNumber) {
            1 -> state.allUserWorldDataList1
            2 -> state.allUserWorldDataList2
            3 -> state.allUserWorldDataList3
            4 -> state.allUserWorldDataList4
            else -> emptyList()
        }
        reduce {
            state.copy(
                clickAllUserData = selectedUser,
                clickAllUserWorldDataList = selectedUserWorldDataList)
        }
    }

    fun onLikeClick() = intent {

        

    }



}

@Immutable
data class CommunityState(
    val userDataList: List<User> = emptyList(),
    val patDataList: List<Pat> = emptyList(),
    val itemDataList: List<Item> = emptyList(),
    val page: Int = 0,
    val allUserDataList: List<AllUser> = emptyList(),
    val allUserData1: AllUser = AllUser(),
    val allUserData2: AllUser = AllUser(),
    val allUserData3: AllUser = AllUser(),
    val allUserData4: AllUser = AllUser(),
    val allUserWorldDataList1: List<String> = emptyList(),
    val allUserWorldDataList2: List<String> = emptyList(),
    val allUserWorldDataList3: List<String> = emptyList(),
    val allUserWorldDataList4: List<String> = emptyList(),
    val situation: String = "world",
    val clickAllUserData: AllUser = AllUser(),
    val clickAllUserWorldDataList: List<String> = emptyList()
    )


//상태와 관련없는 것
sealed interface CommunitySideEffect{
    class Toast(val message:String): CommunitySideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}