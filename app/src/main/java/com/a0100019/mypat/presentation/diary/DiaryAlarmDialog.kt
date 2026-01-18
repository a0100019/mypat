package com.a0100019.mypat.presentation.diary

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryAlarmDialog(
    onClose: () -> Unit = {},
    onConfirmClick: (String) -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("diary_alarm", Context.MODE_PRIVATE) }
    val savedAlarmTime = prefs.getString("alarm_time", "00:00") ?: "00:00"

    val timeParts = savedAlarmTime.split(":")
    val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
    val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    val hours = (0..23).toList()
    val minutes = (0..50 step 10).toList()
    val initialMinuteIndex = (initialMinute / 10).coerceIn(0, minutes.lastIndex)

    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = initialHour)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = initialMinuteIndex)

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .width(320.dp) // 조금 더 컴팩트하게 조절
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 상단 아이콘 (선택 사항)
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "오늘의 기억, 잊지 않도록",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "매일 같은 시간에 알림을 보내드릴게요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 휠 피커 영역 (중앙 강조 가이드 추가)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 선택 영역 하이라이트 배경
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    )

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        WheelPicker(
                            items = hours,
                            state = hourState,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = ":",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(bottom = 2.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        WheelPicker(
                            items = minutes,
                            state = minuteState,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp), // 상단 영역과 약간의 거리두기
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. 취소 (또는 알람 끄기) 버튼: 테두리만 있거나 투명한 스타일
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { onCancelClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "나중에 할래요",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 2. 설정 완료 버튼: 메인 컬러로 강조
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary) // 하루마을 포인트 컬러
                            .clickable {
                                fun getCenteredIndex(state: LazyListState): Int {
                                    val layoutInfo = state.layoutInfo
                                    val containerCenter = (layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset) / 2
                                    return layoutInfo.visibleItemsInfo.minByOrNull {
                                        kotlin.math.abs((it.offset + it.size / 2) - containerCenter)
                                    }?.index ?: state.firstVisibleItemIndex
                                }

                                val h = hours[getCenteredIndex(hourState).coerceIn(0, hours.lastIndex)]
                                val m = minutes[getCenteredIndex(minuteState).coerceIn(0, minutes.lastIndex)]
                                onConfirmClick(String.format("%02d:%02d", h, m))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "설정하기", // '설정'보다 훨씬 부드러운 표현
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<Int>,
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    // 중앙 인덱스 실시간 계산
    val centeredIndex by remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val containerCenter = (layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset) / 2
            if (layoutInfo.visibleItemsInfo.isEmpty()) 0
            else {
                layoutInfo.visibleItemsInfo.minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - containerCenter)
                }?.index ?: 0
            }
        }
    }

    LazyColumn(
        state = state,
        modifier = modifier.height(150.dp),
        contentPadding = PaddingValues(vertical = 60.dp), // 상하단 패딩으로 아이템을 중앙으로 유도
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items.size) { index ->
            val isSelected = centeredIndex == index
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", items[index]),
                    fontSize = if (isSelected) 22.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryAlarmDialogPreview() {
    MypatTheme {
        DiaryAlarmDialog(
            onClose = {},
        )
    }
}