package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import kotlin.random.Random

@Composable
fun LetterViewDialog(
    onClose: () -> Unit,
    onLetterLinkClick: () -> Unit,
    onLetterConfirmClick: () -> Unit,
    clickLetterData: Letter,
    closeVisible: Boolean = true
) {

    Dialog(
        onDismissRequest = {  }
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if(closeVisible){
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "close",
                    modifier = Modifier
                        .clickable { onClose() }
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp) // ⬅️ 둥글게 처리
                        )
                        .align(Alignment.End),
                )
                Spacer(modifier = Modifier.size(6.dp))
            }
            Box(
                modifier = Modifier
                    .background(color = Color.Green)
                    .fillMaxHeight(0.8f)
//                    .fillMaxWidth(0.8f)
            ) {
                
                //편지 이미지

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Column (
                        modifier = Modifier.weight(8f)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .weight(8f)
                                .background(color = Color.Yellow),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text(
                                text = clickLetterData.title,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(10.dp),
                                color = Color.Black
                            )

                            Text(
                                text = clickLetterData.message
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Row(
                                modifier = Modifier
//                            .fillMaxWidth(0.8f)
                            ) {

                                if (clickLetterData.link != "0") {
                                    MainButton(
                                        text = " 링크 이동하기 ",
                                        onClick = onLetterLinkClick
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                MainButton(
                                    onClick = onLetterConfirmClick,
                                    modifier = Modifier,
                                    text = if (clickLetterData.state == "read") {
                                        " 확인 "
                                    } else {
                                        " 확인 (${clickLetterData.reward} +${clickLetterData.amount}) "
                                    }

                                )

                            }

                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.weight(1f))

                }

            }

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = "편지를 아래로 드래그하세요",
                modifier = Modifier
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(12.dp) // ⬅️ 둥글게 처리
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp) // ⬅️ 내부 여백
            )

        }
    }

}

@Preview(showBackground = true)
@Composable
fun LetterViewDialogPreview() {
    MypatTheme {
        LetterViewDialog(
            onClose = {},
            clickLetterData = Letter(state = "open", title = "첫 편지", message = "안녕하세요 저는 이유빈입니다.".repeat(50), link = "naver.com", reward = "cash", amount = "100" ),
            onLetterLinkClick = {},
            onLetterConfirmClick = {}



            )
    }
}