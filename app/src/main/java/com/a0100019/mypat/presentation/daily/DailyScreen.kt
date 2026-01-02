package com.a0100019.mypat.presentation.daily

import android.app.Activity
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
import com.a0100019.mypat.presentation.daily.walk.RequestBatteryPermissionScreen
import com.a0100019.mypat.presentation.daily.walk.RequestNotificationPermissionScreen
import com.a0100019.mypat.presentation.daily.walk.RequestPermissionScreen
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
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
    onKnowledgeNavigateClick: () -> Unit = {},
    popBackStack: () -> Unit
) {

    val dailyState : DailyState = dailyViewModel.collectAsState().value

    val context = LocalContext.current

    val activity = context as Activity

    dailyViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DailySideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            DailySideEffect.NavigateToWalkScreen -> onWalkNavigateClick()

            DailySideEffect.ShowRewardAd -> {
                dailyViewModel.showRewardAd(activity)
            }

        }
    }

    DailyScreen(
//        onWalkNavigateClick = { dailyViewModel.walkPermissionCheck(context) },
        onDiaryNavigateClick = onDiaryNavigateClick,
        onEnglishNavigateClick = onEnglishNavigateClick,
        onKoreanNavigateClick = onKoreanNavigateClick,
        onKnowledgeNavigateClick = onKnowledgeNavigateClick,
        onCloseClick = dailyViewModel::onCloseClick,
//        onDialogPermissionCheckClick = dailyViewModel::onDialogPermissionCheckClick,
//        onDialogNotificationPermissionCheckClick = dailyViewModel::onDialogNotificationPermissionCheckClick,
//        onDialogBatteryOptimizationPermissionCheckClick = dailyViewModel::onDialogBatteryOptimizationPermissionCheckClick,
        popBackStack = popBackStack,
        onAdClick = dailyViewModel::onAdClick,
        onSituationChange = dailyViewModel::onSituationChange,

        rewardAdReady = dailyState.rewardAdReady,
        situation = dailyState.situation,
    )

}

@Composable
fun DailyScreen(
    situation: String = "",
    rewardAdReady: Boolean = false,

//    onWalkNavigateClick: () -> Unit,
    onDiaryNavigateClick: () -> Unit,
    onEnglishNavigateClick: () -> Unit,
    onKoreanNavigateClick: () -> Unit,
    onKnowledgeNavigateClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
//    onDialogPermissionCheckClick: (Context) -> Unit = {},
//    onDialogNotificationPermissionCheckClick: (Context) -> Unit = {},
//    onDialogBatteryOptimizationPermissionCheckClick: (Context) -> Unit = {},
    popBackStack: () -> Unit = {},
    onAdClick: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
) {

//    val context = LocalContext.current
//    val tutorialPrefs = context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
//    val tutorial = tutorialPrefs.getString("tutorial", "미션")
//    if(tutorial == "미션") {
//        tutorialPrefs.edit().putString("tutorial", "커뮤니티").apply()
//    }

//    if(situation == "walkPermissionRequest") {
//        RequestPermissionScreen()
//    } else if (situation in listOf("walkPermissionSetting", "walkPermissionSettingNo")) {
//        WalkPermissionDialog(
//            situation = situation,
//            onCloseClick = onCloseClick,
//            onCheckClick = onDialogPermissionCheckClick
//        )
//    } else if (situation == "notificationPermissionRequest") {
//        RequestNotificationPermissionScreen()
//    } else if (situation in listOf("notificationPermissionSetting", "notificationPermissionSettingNo")) {
//        NotificationPermissionDialog(
//            situation = situation,
//            onCloseClick = onCloseClick,
//            onCheckClick = onDialogNotificationPermissionCheckClick
//        )
//    } else if (situation == "batteryPermissionRequest") {
//        RequestBatteryPermissionScreen()
//    } else if (situation in listOf("batteryPermissionSetting", "batteryPermissionSettingNo")) {
//        BatteryPermissionDialog(
//            situation = situation,
//            onCloseClick = onCloseClick,
//            onCheckClick = onDialogBatteryOptimizationPermissionCheckClick
//        )
//    }

    when(situation) {
        "adCheck" -> SimpleAlertDialog(
            onConfirmClick = {
                onAdClick()
                onSituationChange("")
                             },
            onDismissClick = onCloseClick,
            text = "광고를 보고 1햇살을 얻겠습니까?",
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
        ){

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                        ,
                contentAlignment = Alignment.Center
            ) {
                // 가운데 텍스트
                Text(
                    text = "하루 미션",
                    style = MaterialTheme.typography.displaySmall
                )

                // 오른쪽 버튼
                MainButton(
                    text = "닫기",
                    onClick = popBackStack,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Text(
                text = "매일 꾸준히 하루미션들을 완료하여 멋있는 사람이 되어보세요!\n하루미션을 완료할 때마다 햇살을 얻을 수 있습니다",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 36.dp, bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp) // ← 아이템 사이 간격
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
                                indication = null,
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
                                indication = null,
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
                                    text = "매우 쉬우니 걱정하지 마세요",
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
                                indication = null,
                                onClick = onKnowledgeNavigateClick
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
                                    text = "상식",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "필수 지식들을 공부해봐요",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "외워두면 지식인이 될 수 있어요",
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
                                indication = null,
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
                                    text = "어려워요! 천천히 회이팅! 응원할게요",
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
//                                indication = null,
//                                onClick = onWalkNavigateClick
//                            )
//                            .padding(top = 6.dp, bottom = 6.dp)
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
//                                    text = "만보기",
//                                    style = MaterialTheme.typography.headlineMedium,
//                                    modifier = Modifier
//                                        .padding(bottom = 10.dp)
//                                    ,
//                                )
//                                Text(
//                                    text = "건강과 함께 마을을 키워보세요",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier
//                                        .padding(bottom = 10.dp)
//                                    ,
//                                )
//                                Text(
//                                    text = "언제든지 설정에서 만보기 기능을 정지할 수 있습니다",
//                                    style = MaterialTheme.typography.titleSmall,
//                                    modifier = Modifier,
//                                )
//                            }
//                        }
//
//                    }
//                }

                if(rewardAdReady){
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
                                    indication = null,
                                    onClick = { onSituationChange("adCheck") }
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
                                        text = "광고보기",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier
                                            .padding(bottom = 10.dp),
                                    )
                                    Text(
                                        text = "하루에 1회만 가능합니다",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .padding(bottom = 10.dp),
                                    )
                                    Text(
                                        text = "광고를 보면 1 햇살을 얻을 수 있습니다!",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier,
                                    )
                                }
                            }

                        }
                    }
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyScreenPreview() {
    MypatTheme {
        DailyScreen(
            onDiaryNavigateClick = {  },
            onEnglishNavigateClick = {  },
            onKoreanNavigateClick = {  },
            situation = "",
            rewardAdReady = true
        )
    }
}