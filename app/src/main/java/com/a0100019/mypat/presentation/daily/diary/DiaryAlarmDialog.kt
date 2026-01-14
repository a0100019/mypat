package com.a0100019.mypat.presentation.daily.diary

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val prefs = context.getSharedPreferences("diary_alarm", Context.MODE_PRIVATE)
    val savedAlarmTime: String = prefs.getString("alarm_time", "00:00") ?: "00:00"

    // 1. 초기값 파싱
    val timeParts = savedAlarmTime.split(":")
    val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
    val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    val hours = (0..23).toList()
    val minutes = (0..50 step 10).toList()

    // 초기 인덱스 계산 (10분 단위 내림 처리)
    val initialMinuteIndex = (initialMinute / 10).coerceIn(0, minutes.lastIndex)

    // LazyListState 설정
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = initialHour)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = initialMinuteIndex)

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "알림 시간 설정",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 휠 피커 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    WheelPicker(
                        items = minutes,
                        state = minuteState,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MainButton(
                        text = " 취소 ",
                        onClick = onCancelClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MainButton(
                        text = " 확인 ",
                        onClick = {
                            // 중앙에 있는 아이템의 인덱스를 계산하는 헬퍼 함수
                            fun getCenteredIndex(state: LazyListState): Int {
                                val layoutInfo = state.layoutInfo
                                val containerCenter = (layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset) / 2
                                return layoutInfo.visibleItemsInfo.minByOrNull {
                                    kotlin.math.abs((it.offset + it.size / 2) - containerCenter)
                                }?.index ?: state.firstVisibleItemIndex
                            }

                            val hIdx = getCenteredIndex(hourState)
                            val mIdx = getCenteredIndex(minuteState)

                            val h = hours[hIdx.coerceIn(0, hours.lastIndex)]
                            val m = minutes[mIdx.coerceIn(0, minutes.lastIndex)]

                            onConfirmClick(String.format("%02d:%02d", h, m))
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    )
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