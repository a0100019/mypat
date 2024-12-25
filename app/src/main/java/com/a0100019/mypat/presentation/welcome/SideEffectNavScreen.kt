package com.a0100019.mypat.presentation.welcome


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SideEffectNavScreen(
    viewModel: SideEffectNavViewModel = hiltViewModel(),
    onNavigateToSelectScreen: () -> Unit
) {
//    val state = viewModel.collectAsState().value

    val context = LocalContext.current

    //뷰모델에서 버튼을 누른 뒤 화면 전환 하기 전에 계산할 게 있기 때문에 필요한 내용
    //일단 뷰 모델을 거치면 네비게이션 힘수는 screen에서 처리하더라도 아래의 사이드이펙트 코드가 필요함
    //만약 LoginScreen이 SignUpScreen으로 이돌할 때 처럼 그냥 버튼 누르면 바로 이동할 때는 사이드 이펙트로 뺄 필요 없음
    viewModel.collectSideEffect{ sideEffect ->
        when(sideEffect){
            is SideEffectNavSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            SideEffectNavSideEffect.NavigateToSelectScreen -> onNavigateToSelectScreen()
        }
    }

    SideEffectNavScreen(
        onNavigateToSelectScreen = viewModel::onMoveSelectClick,
        test = ""
    )

}

@Composable
fun SideEffectNavScreen(
    onNavigateToSelectScreen: () -> Unit,
    test: String
) {
    Surface {
        Column {
            Text("환영합니다")
            Button(
                onClick = {
                    onNavigateToSelectScreen()
                }
            ) {
                Text("SelectScreen으로 이동")
            }
        }
    }
}

@Preview
@Composable
fun SideEffectNavScreenPreview() {
    MypatTheme {
        SideEffectNavScreen(
            onNavigateToSelectScreen = {},
            test = ""
        )
    }
}