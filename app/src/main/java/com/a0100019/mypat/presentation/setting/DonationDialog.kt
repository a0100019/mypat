package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DonationDialog(
    onClose: () -> Unit = {},
    donationList: List<Donation> = emptyList(),
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight(0.9f)
            ) {

                // ── 제목 ──
                Text(
                    text = "방명록",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

//                Text(
//                    text = "${donationList.size}개의 메시지",
//                    style = MaterialTheme.typography.labelMedium,
//                    color = Color.Gray
//                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── 📜 방명록 리스트 (인라인 카드) ──
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(donationList.reversed()) { donation ->

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFF8F8F8),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE2E2E2),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp)
                        ) {

                            // ── 상단: 이름 · 태그 · 날짜 ──
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = donation.name,
                                        fontSize = 14.sp,
                                        color = Color(0xFF333333)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = "#${donation.tag}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF777777)
                                    )
                                }

                                Text(
                                    text = donation.date,
                                    fontSize = 11.sp,
                                    color = Color(0xFF999999)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // ── 메시지 ──
                            Text(
                                text = donation.message,
                                fontSize = 13.sp,
                                color = Color(0xFF444444),
                                lineHeight = 18.sp,
                                maxLines = 3,                       // ⬅️ 3줄 제한
                                overflow = TextOverflow.Ellipsis    // ⬅️ 넘치면 …
                            )

                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // ── 확인 버튼 ──
                MainButton(
                    text = "확인",
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DonationDialogPreview() {
    MypatTheme {
        DonationDialog(
            onClose = {},
            donationList = listOf(Donation(name = "name", tag = "13", message = "안".repeat(80), date = "2025-11-11"))
        )
    }
}