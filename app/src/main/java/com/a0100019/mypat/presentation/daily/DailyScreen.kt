package com.a0100019.mypat.presentation.daily

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.daily.walk.RequestPermissionScreen
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DailyScreen(
    dailyViewModel: DailyViewModel = hiltViewModel(),
    onWalkNavigateClick: () -> Unit,
    onDiaryNavigateClick: () -> Unit,
    onEnglishNavigateClick: () -> Unit,
    onKoreanNavigateClick: () -> Unit,
    popBackStack: () -> Unit
) {

    val dailyState : DailyState = dailyViewModel.collectAsState().value

    val context = LocalContext.current

    dailyViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DailySideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            DailySideEffect.NavigateToWalkScreen -> onWalkNavigateClick()
        }
    }

    DailyScreen(
        onWalkNavigateClick = { dailyViewModel.walkPermissionCheck(context) },
        onDiaryNavigateClick = onDiaryNavigateClick,
        onEnglishNavigateClick = onEnglishNavigateClick,
        onKoreanNavigateClick = onKoreanNavigateClick,
        onCloseClick = dailyViewModel::onCloseClick,
        onDialogPermissionCheckClick = dailyViewModel::onDialogPermissionCheckClick,
        popBackStack = popBackStack,

        situation = dailyState.situation,
    )

}

@Composable
fun DailyScreen(
    onWalkNavigateClick: () -> Unit,
    onDiaryNavigateClick: () -> Unit,
    onEnglishNavigateClick: () -> Unit,
    onKoreanNavigateClick: () -> Unit,
    onCloseClick: () -> Unit = {},
    onDialogPermissionCheckClick: (Context) -> Unit = {},
    situation: String,
    popBackStack: () -> Unit = {}
) {

    if(situation == "walkPermissionRequest") {
        RequestPermissionScreen()
    } else if (situation in listOf("walkPermissionSetting", "walkPermissionSettingNo")) {
        WalkPermissionDialog(
            situation = situation,
            onCloseClick = onCloseClick,
            onCheckClick = onDialogPermissionCheckClick
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                        ,
                contentAlignment = Alignment.Center
            ) {
                // 가운데 텍스트
                Text(
                    text = "Daily",
                    style = MaterialTheme.typography.displaySmall
                )

                // 오른쪽 버튼
                MainButton(
                    text = "닫기",
                    onClick = popBackStack,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }


            LazyColumn (
                modifier = Modifier
                    .padding(top = 24.dp)
                    .weight(1f)
            ){

                item {
                    //버튼 기본 설정
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.scrim,
                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(bounded = true, color = Color.White),
                                onClick = onWalkNavigateClick
                            )
                            .padding(top = 6.dp, bottom = 6.dp)
                    ) {
                        Box {

                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "만보기",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "매일 만보를 걸어 보상을 획득하세요!",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "하루가 지나면 보상을 획득할 수 없습니다",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier,
                                )
                            }
                        }

                    }
                }

                item {
                    //버튼 기본 설정
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.scrim,
                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(bounded = true, color = Color.White),
                                onClick = onEnglishNavigateClick
                            )
                            .padding(top = 6.dp, bottom = 6.dp)
                    ) {
                        Box {

                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "영단어",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "목표 영단어를 추측을 통해 맞춰주세요",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "기회는 무제한이니 걱정하지 마세요!",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier,
                                )
                            }
                        }

                    }
                }

                item {
                    //버튼 기본 설정
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.scrim,
                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(bounded = true, color = Color.White),
                                onClick = onKoreanNavigateClick
                            )
                            .padding(top = 6.dp, bottom = 6.dp)
                    ) {
                        Box {

                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "사자성어",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "한자 카드를 조합하여 사자성어를 맞춰주세요",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "기회는 무제한이니 걱정하지 마세요!",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier,
                                )
                            }
                        }

                    }
                }

                item {
                    //버튼 기본 설정
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.scrim,
                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(bounded = true, color = Color.White),
                                onClick = onDiaryNavigateClick
                            )
                            .padding(top = 6.dp, bottom = 6.dp)
                    ) {
                        Box {

                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "일기",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "오늘 하루를 정리하세요",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "길게 적지 않아도 돼요. 꾸준함이 중요합니다",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier,
                                )
                            }
                        }

                    }
                }

//                item {
//                    //버튼 기본 설정
//                    val interactionSource = remember { MutableInteractionSource() }
//                    val isPressed by interactionSource.collectIsPressedAsState()
//                    val scale by animateFloatAsState(
//                        targetValue = if (isPressed) 0.95f else 1f,
//                        label = "scale"
//                    )
//
//                    Surface(
//                        shape = RoundedCornerShape(16.dp),
//                        color = MaterialTheme.colorScheme.scrim,
//                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
//                        modifier = Modifier
//                            .graphicsLayer {
//                                scaleX = scale
//                                scaleY = scale
//                            }
//                            .clickable(
//                                interactionSource = interactionSource,
//                                indication = rememberRipple(bounded = true, color = Color.White),
//                                onClick = {  }
//                            )
//                            .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
//                    ) {
//                        Box {
//
//                            Column(
//                                modifier = Modifier
//                                    .padding(8.dp)
//                                    .fillMaxWidth(),
//                                verticalArrangement = Arrangement.Center,
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(
//                                    text = "준비중...",
//                                    style = MaterialTheme.typography.headlineMedium,
//                                    modifier = Modifier
//                                        .padding(top = 20.dp, bottom = 20.dp)
//                                    ,
//                                )
//
//                            }
//                        }
//
//                    }
//                }

            }

            Text(
                text = "만보기를 제외한 미션들은 다른 날에 미션을 완료해도 보상을 획득할 수 있습니다",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(30.dp)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyScreenPreview() {
    MypatTheme {
        DailyScreen(
            onWalkNavigateClick = {  },
            onDiaryNavigateClick = {  },
            onEnglishNavigateClick = {  },
            onKoreanNavigateClick = {  },
            situation = ""
        )
    }
}