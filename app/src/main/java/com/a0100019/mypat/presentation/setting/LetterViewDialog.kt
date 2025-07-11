package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
) {

    Dialog(
        onDismissRequest = onClose
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "close",
                modifier = Modifier
                    .clickable { onClose() }
                    .align(Alignment.End)
                ,
            )

            Box(
                modifier = Modifier
                    .background(color = Color.Green)
            ) {
                
                //편지 이미지


                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                            text = if(clickLetterData.state == "read") {
                                " 확인 "
                            } else {
                                " 확인 (${clickLetterData.reward} +${clickLetterData.amount}) "
                            }
                            
                        )

                    }

                }
            }

            Text(
                text = "편지를 아래로 드래그하세요"
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
            clickLetterData = Letter(state = "open", title = "첫 편지", message = "안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n", link = "naver.com", reward = "cash", amount = "100" ),
            onLetterLinkClick = {},
            onLetterConfirmClick = {}



            )
    }
}