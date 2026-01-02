package com.a0100019.mypat.presentation.privateChat

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun PrivateChatGameRankDialog(
    onClose: () -> Unit = {},
    privateChatTotalRankList: List<PrivateRoom> = emptyList(),
) {
    var page by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight(0.8f)
                .shadow(12.dp, shape = RoundedCornerShape(24.dp))
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🏆 누적 점수 순위",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    itemsIndexed(
                        privateChatTotalRankList
                    ) { index, room ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF9FAFB) // 연한 파스텔 배경
                            ),
                            border = BorderStroke(
                                1.dp,
                                Color(0xFFE0E0E0) // 부드러운 테두리
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {

                                // 1줄 : 순위 + 유저
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // 순위 강조
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5C6BC0), // 파스텔 인디고
                                        modifier = Modifier.width(26.dp)
                                    )

                                    Text(
                                        text = "${room.name1} #${room.user1} · ${room.name2} #${room.user2}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF333333),
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // 2줄 : 점수들
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
//
//                                    Text(
//                                        text = "최고 ${room.highScore}점",
//                                        style = MaterialTheme.typography.labelMedium,
//                                        color = Color(0xFF43A047) // 파스텔 그린
//                                    )

                                    Text(
                                        text = "누적 ${room.totalScore}점",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFFFB8C00) // 파스텔 오렌지
                                    )
                                }
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
//                    MainButton(
//                        text = if(page == 0) "누적 점수" else "최고 점수",
//                        onClick = {
//                            if(page == 0) page = 1 else page = 0
//                        },
//                        modifier = Modifier.padding(top = 8.dp)
//                    )

                    Spacer(modifier = Modifier.weight(1f))

                    MainButton(
                        text = "확인",
                        onClick = onClose,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivateChatGameRankDialogPreview() {
    MypatTheme {
        PrivateChatGameRankDialog(
            onClose = {},
            privateChatTotalRankList = listOf(
                PrivateRoom(
                    roomId = "room1",
                    user1 = "101",
                    user2 = "202",
                    name1 = "유저A",
                    name2 = "유저B",
                    highScore = 120,
                    totalScore = 3400,
                    attacker = "101"
                ),
                PrivateRoom(
                    roomId = "room2",
                    user1 = "303",
                    user2 = "404",
                    name1 = "유저C",
                    name2 = "유저D",
                    highScore = 98,
                    totalScore = 2100,
                    attacker = "303"
                ),
                PrivateRoom(
                    roomId = "room3",
                    user1 = "505",
                    user2 = "606",
                    name1 = "유저E",
                    name2 = "유저F",
                    highScore = 77,
                    totalScore = 1500,
                    attacker = "606"
                )
            )

        )
    }
}
