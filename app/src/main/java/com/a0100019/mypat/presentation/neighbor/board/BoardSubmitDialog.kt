package com.a0100019.mypat.presentation.neighbor.board

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardSubmitDialog(
    text: String = "",
    anonymous: String = "0",
    type: String = "free",
    onClose: () -> Unit = {},
    onChangeAnonymousClick: (String) -> Unit = {},
    onChangeTypeClick: (String) -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {
    // 드롭다운 펼침 상태 관리
    var expanded by remember { mutableStateOf(false) }

    // 타입에 따른 한글 명칭 매핑
    val typeMap = mapOf(
        "free" to "자유",
        "worry" to "고민",
        "congratulation" to "축하",
        "friend" to "친구 구하기"
    )

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .width(340.dp)
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
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "게시글 작성",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp),
                )

                // --- 익명 여부 및 카테고리 선택 Row ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 익명 체크박스
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = anonymous == "1",
                            onCheckedChange = { onChangeAnonymousClick(if (it) "1" else "0") }
                        )
                        Text(text = "익명")
                    }

                    // --- 드롭다운 영역 ---
                    Box {
                        Surface(
                            onClick = { expanded = true },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = typeMap[type] ?: "선택",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                // 화살표 아이콘 (이전 답변의 회전 로직 활용 가능)
                                // 드롭다운 내부의 아이콘 영역
                                JustImage(
                                    filePath = "etc/arrow.png",
                                    modifier = Modifier
                                        .size(15.dp)
                                        .graphicsLayer {
                                            // 메뉴가 열려있을(expanded) 때는 180도 회전해서 위를 보게 함
                                            rotationZ = 180f
                                        }
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            typeMap.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        onChangeTypeClick(key)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("내용") },
                    placeholder = { Text("내용을 입력하세요.") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(8.dp),
                    maxLines = Int.MAX_VALUE
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "부적절한 내용을 작성할 경우, 경고 없이 제제를 받을 수 있습니다",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Row {
                    MainButton(
                        text = " 취소 ",
                        onClick = onClose,
                        modifier = Modifier.padding(16.dp)
                    )
                    MainButton(
                        text = " 확인 ",
                        onClick = onConfirmClick,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BoardSubmitDialogPreview() {
    MypatTheme {
        BoardSubmitDialog(
        )
    }
}