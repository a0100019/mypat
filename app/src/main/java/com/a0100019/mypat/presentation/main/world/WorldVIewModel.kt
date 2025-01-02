package com.a0100019.mypat.presentation.main.world

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.pet.Pat
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
class WorldViewModel @Inject constructor(
    private val userDao: UserDao,
    private val worldDao: WorldDao,

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
        // Flow 데이터를 State로 업데이트
        val mapData = worldDao.getWorldDataById("map")
        reduce {
            state.copy(mapData = mapData)
        }

    }

//
//    fun getBitmap(): Bitmap {
//        return loadBitmapFromAssets(context, "koreanIdiomImage/jukmagow1.jpg")
//    }

//    fun onDailyNavigateClick() = intent {
//        postSideEffect(MainSideEffect.NavigateToDailyActivity)
//    }


}

@Immutable
data class WorldState(
    val id:String = "",
    val password:String = "",
    val worldData: List<World> = emptyList(),
    val mapData: World? = null,
    val firstPatData: Pat = Pat(url = ""),
    val firstPatWorldData: World = World(id = "")
    )


//상태와 관련없는 것
sealed interface WorldSideEffect{
    class Toast(val message:String): WorldSideEffect
//    data object NavigateToDailyActivity: WorldSideEffect

}