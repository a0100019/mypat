package com.a0100019.mypat.presentation.community.operator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.community.ChatMessage
import com.a0100019.mypat.presentation.community.getPastelColorForTag
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommunityAskViewDialog(
    onClose: () -> Unit = {},
    askMessages: List<ChatMessage> = emptyList(),
    onAskClick: (String) -> Unit = {},
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
//                .fillMaxHeight(0.5f)
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
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "도란도란",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(10.dp),
                )

                // 추가로 원하는 Composable 요소

                if(askMessages.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp, end = 6.dp, top = 12.dp),
                        reverseLayout = true,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(askMessages.reversed()) { index, message ->

                            val bubbleColor = getPastelColorForTag(message.tag)

                            val textColor = Color.Black

                            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                            val today = dateFormat.format(Date())

                            val prevDate = askMessages.reversed().getOrNull(index - 1)
                            val currentDate = dateFormat.format(Date(message.timestamp))
                            val previousDate = prevDate?.let { dateFormat.format(Date(it.timestamp)) }

                            // 📅 날짜 구분선
                            if (currentDate != previousDate && currentDate != today) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREA)
                                            .format(Date(message.timestamp)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .widthIn(max = 280.dp)
                                                .padding(horizontal = 8.dp),
                                        ) {
                                            Row {
                                                Row(
                                                ) {
                                                    Text(
                                                        text = message.name,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                                    )
                                                    Text(
                                                        text = "#" + message.tag,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                                    )
                                                }

                                                val time = remember(message.timestamp) {
                                                    SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
                                                        .format(Date(message.timestamp))
                                                }

                                                Text(
                                                    text = time,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                                )

                                            }

                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        bubbleColor,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(8.dp)
                                            ) {
                                                Text(
                                                    text = message.message,
                                                    modifier = Modifier
                                                        .clickable {
                                                            onAskClick(message.message)
                                                        }
                                                    )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                Row {
                    MainButton(
                        text = " 취소 ",
                        onClick = onClose,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityAskViewDialogPreview() {
    MypatTheme {
        CommunityAskViewDialog(
            onClose = {},
            onAskClick = {},
            askMessages = listOf(ChatMessage(10202020, "a", "a", tag = "1", ban = "0", uid = "hello"))
        )
    }
}