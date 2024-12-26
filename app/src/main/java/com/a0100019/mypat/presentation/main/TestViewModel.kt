package com.a0100019.mypat.presentation.main


import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.domain.CombineNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val combineNumberUseCase: CombineNumberUseCase
) : ViewModel(), ContainerHost<TestState, TestSideEffect> {

    //sideEffect 는 토스트, 네비게이션과 같이 단발성 이벤트 처리하기 위함.
    override val container: Container<TestState, TestSideEffect> =
        container(
            //state 사용 하기 위한 것
            initialState = TestState(),
            //toast 사용 하기 위한 것
            buildSettings = {
                this.exceptionHandler = CoroutineExceptionHandler {_, throwable ->
                    intent { postSideEffect(TestSideEffect.Toast(throwable.message.orEmpty())) }
                }
            }
        )

    // 이 안에 함수 넣으면 그 함수는 뷰모델 생성 되자마자 실행
    init {

    }

    //아이디 입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onFirstNumberChange(firstNumber: String) = blockingIntent {
        reduce {
            state.copy(firstNumber = firstNumber)
        }

    }

    //reduce는 상태를 가져오고 변경한 상태를 업데이트하기위한 함수
    //state.copy는 data class의 기능으로 일부 값을 변경한 새로운 상태를 반환
    @OptIn(OrbitExperimental::class)
    fun onSecondNumberChange(secondNumber: String) = blockingIntent {
        reduce {
            state.copy(secondNumber = secondNumber)
        }

    }

    @OptIn(OrbitExperimental::class)
    fun onOperationChange(operation: String) = blockingIntent {
        reduce {
            state.copy(operation = operation)
        }
    }

    // 버튼 클릭 시 계산
    fun onCombineNumbers() = intent {
        val combinedValue = combineNumberUseCase(state.firstNumber, state.secondNumber, state.operation)
        reduce {
            state.copy(result = combinedValue) // 결과를 상태에 저장
        }
        postSideEffect(TestSideEffect.Toast(message = "계산 완료!"))
    }

}

//뷰모델에서 관리할 텍스트들
@Immutable
data class TestState(
    val firstNumber:String = "",
    val secondNumber:String = "",
    val result:String = "",
    val operation: String = ""
)

//상태와 관련없는 것
sealed interface TestSideEffect{
    class Toast(val message:String): TestSideEffect
    //screen도 되고 엑티비티도 됨
//    object NavigateToMainActivity:MainSideEffect
}