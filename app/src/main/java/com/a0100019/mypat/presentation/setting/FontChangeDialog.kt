package com.a0100019.mypat.presentation.setting

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun FontChangeDialog(
    onClose: () -> Unit = {},
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
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "폰트 변경하기",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(10.dp),
                )

                Text(
                    text = "원하는 폰트를 선택한 후 앱을 다시 시작하면 폰트가 적용됩니다",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(10.dp)
                    ,
                )

                Spacer(modifier = Modifier.size(12.dp))

                val context = LocalContext.current
                val prefs = context.getSharedPreferences("font_prefs", Context.MODE_PRIVATE)

                // UI에서 현재 선택된 값을 보여주기 위해 상태(State)를 하나 선언하면 좋습니다.
                // (실시간 반영이 안 되더라도, 체크 표시 등을 할 때 유용합니다.)
                var currentFont by remember { mutableStateOf(prefs.getString("font_key", "pretendard")) }

                Text(
                    text = "pretendard 글씨체",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily(Font(R.font.pretendard))),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            // 1. 프리퍼런스에 "pretendard" 저장
                            prefs.edit().putString("font_key", "pretendard").apply()
                            currentFont = "pretendard"
                            Toast.makeText(context, "기본 서체로 설정되었습니다. (재시작 시 적용)", Toast.LENGTH_SHORT).show()
                        },
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = "온글잎 따콩체",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily(Font(R.font.letter))),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            // 2. 프리퍼런스에 "letter" 저장
                            prefs.edit().putString("font_key", "letter").apply()
                            currentFont = "letter"
                            Toast.makeText(context, "따콩체로 설정되었습니다. (재시작 시 적용)", Toast.LENGTH_SHORT).show()
                        },
                )

                MainButton(
                    text = " 닫기 ",
                    onClick = onClose,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.End)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FontChangeDialogPreview() {
    MypatTheme {
        FontChangeDialog(
        )
    }
}