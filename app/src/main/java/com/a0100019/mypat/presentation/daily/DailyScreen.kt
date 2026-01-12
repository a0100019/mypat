package com.a0100019.mypat.presentation.daily

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
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
            text = "광고를 보고 1 햇살을 얻겠습니까?",
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
                .padding(horizontal = 20.dp)
        ) {
            // 상단 헤더 영역
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "하루 미션",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                // 닫기 버튼을 아이콘 버튼으로 변경하여 세련되게 수정 가능
                MainButton(
                    text = "마을",
                    onClick = popBackStack,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

                MainButton(
                    text = "종료",
                    onClick = {},
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Text(
                text = "매일 꾸준히 하루 미션을 완료하여 멋있는 사람이 되어보세요!\n미션을 완료할 때마다 햇살을 얻을 수 있습니다",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )

            // 미션 리스트
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                item {
                    MissionCard(
                        title = "상식",
                        description = "필수 지식들을 공부해봐요",
                        subDescription = "외워두면 언젠간 쓸 일이 있을 거에요",
                        icon = "💡",
                        onClick = onKnowledgeNavigateClick
                    )
                }

                item {
                    MissionCard(
                        title = "영단어",
                        description = "목표 영단어를 추측해보세요",
                        subDescription = "어렵지만 끝까지 파이팅!",
                        icon = "🇬🇧",
                        onClick = onEnglishNavigateClick
                    )
                }

                item {
                    MissionCard(
                        title = "사자성어",
                        description = "한자 카드를 조합하여 맞춰보세요",
                        subDescription = "매우 쉬우니 걱정하지 마세요",
                        icon = "📜",
                        onClick = onKoreanNavigateClick
                    )
                }

                item {
                    MissionCard(
                        title = "일기",
                        description = "오늘 하루를 정리하세요",
                        subDescription = "길게 적지 않아도 돼요. 꾸준함이 중요합니다",
                        icon = "✍️", // 이모지를 활용하거나 ImageVector 사용
                        onClick = onDiaryNavigateClick
                    )
                }

                if (rewardAdReady) {
                    item {
                        MissionCard(
                            title = "보너스 햇살 받기",
                            description = "광고 보고 1 햇살 얻기",
                            subDescription = "하루에 한 번만 가능해요",
                            icon = "☀️",
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            onClick = { onSituationChange("adCheck") }
                        )
                    }
                }
            }
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