package com.a0100019.mypat.presentation.activity.daily

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
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
    onDiaryNavigateClick: () -> Unit,
    onEnglishNavigateClick: () -> Unit,
    onKoreanNavigateClick: () -> Unit,
    onKnowledgeNavigateClick: () -> Unit = {},
    popBackStack: () -> Unit = {},
    onAdClick: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    // 다이얼로그 로직
    if (situation == "adCheck") {
        SimpleAlertDialog(
            onConfirmClick = {
                onAdClick()
                onSituationChange("")
            },
            onDismissClick = { onSituationChange("") },
            text = "광고를 보고 3 햇살을 얻겠습니까? 신규 앱 특성상 광고가 부족할 수 있습니다.",
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BackGroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 12.dp)
            ,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 상단 헤더 영역
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "하루 미션",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                JustImage(
                    filePath = "etc/exit.png",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(30.dp)
                        .clickable {
                            popBackStack()
                        }
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                verticalArrangement = Arrangement.spacedBy(16.dp) // 카드 사이 간격도 살짝 넓힘
            ) {
                val missionItems = listOf(
                    Triple("상식", "💡", "필수 지식 배우기") to onKnowledgeNavigateClick,
                    Triple("영단어", "🇬🇧", "목표 영단어 추측") to onEnglishNavigateClick,
                    Triple("사자성어", "📜", "한자 카드 조합") to onKoreanNavigateClick
                )

                missionItems.forEach { (data, onClick) ->
                    val (title, icon, description) = data
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
                        shape = RoundedCornerShape(24.dp), // 더 둥글게 해서 큰 카드에 어울리게 수정
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp), // 패딩을 16 -> 24로 키워 카드 크기 확장
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(icon, fontSize = 32.sp) // 아이콘 크기도 살짝 업
                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) // 제목 강조
                                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            JustImage(
                                filePath = "etc/moon.png",
                                modifier = Modifier
                                    .size(18.dp)

                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                            ) {

                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+1000", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFF673AB7))
                            }
                        }
                    }
                }

//                if (rewardAdReady) {
//                    val interactionAd = remember { MutableInteractionSource() }
//                    val isPressedAd by interactionAd.collectIsPressedAsState()
//                    val scaleAd by animateFloatAsState(if (isPressedAd) 0.96f else 1f, label = "scale")
//
//                    Surface(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .graphicsLayer { scaleX = scaleAd; scaleY = scaleAd }
//                            .clickable(interactionSource = interactionAd, indication = null, onClick = { onSituationChange("adCheck") }),
//                        shape = RoundedCornerShape(24.dp),
//                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f), // 광고 색상 조금 더 진하게
//                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
//                    ) {
//                        Row(
//                            modifier = Modifier.padding(24.dp), // 패딩 동일하게 24로 확장
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text("☀️", fontSize = 32.sp)
//                            Spacer(modifier = Modifier.width(20.dp))
//
//                            Column(modifier = Modifier.weight(1f)) {
//                                Text("보너스 햇살 받기", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
//                                Text("하루에 한 번만 가능합니다", style = MaterialTheme.typography.bodyMedium)
//                            }
//
//                            JustImage(
//                                filePath = "etc/sun.png",
//                                modifier = Modifier
//                                    .size(18.dp)
//
//                            )
//
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier
//                                    .padding(start = 4.dp)
//                            ) {
//
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Text("+3", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFF673AB7))
//                            }
//                        }
//                    }
//                }
            }

            Text(
                text = "매일 하루 미션을 완료하여 마을을 키워보세요",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )



        }
    }
}

@Composable
fun MissionCard(
    title: String,
    description: String,
    subDescription: String,
    icon: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "scale"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically // 모든 요소를 세로 중앙 정렬
        ) {
            // 1. 왼쪽 아이콘 박스
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. 중간 텍스트 영역 (weight를 주어 화살표를 오른쪽 끝으로 밀어냅니다)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subDescription,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 3. 오른쪽 화살표 아이콘 (다시 추가됨!)
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "상세보기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(28.dp)
            )
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