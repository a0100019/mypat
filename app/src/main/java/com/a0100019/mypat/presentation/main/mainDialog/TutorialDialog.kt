package com.a0100019.mypat.presentation.main.mainDialog

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun TutorialDialog(
    onClose: () -> Unit = {},
    state: String = "하루미션"
) {

    Dialog(
        onDismissRequest = {  }
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(240.dp)
        ) {

            Box(
                modifier = Modifier
                    .wrapContentHeight()      // 🔥 전체가 내용 높이에 따라 늘어남
                    .fillMaxWidth()
            ) {

                // 편지 이미지 (내용 높이에 맞게 늘어남)
                JustImage(
                    filePath = "etc/letter.webp",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .matchParentSize()    // Box의 높이에 자동 맞춤
                )

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        modifier = Modifier
                            .weight(8f)
                            .padding(vertical = 16.dp) // 내부 여백
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "간단 튜토리얼",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.size(12.dp))

                        when(state){
                            "하루미션" -> {
                                Text(
                                    text = "하루마을의 에너지원은 바로 관리인의 성실함입니다. 하루마을에는 현재 총 4가지의 간단한 하루 미션이 있습니다. " +
                                            "아래와 같이 생긴 하루 미션 버튼을 눌러 원하는 미션들을 완료하고 주된 화폐인 햇살을 얻어주세요\n\n미션 : 하루 미션 한 개 완료하기",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {},
                                    text = "     하루 미션     "
                                )
                            }
                            "펫" -> {
                                Text(
                                    text = "펫과 놀아주세요! 10분마다 펫 머리 위에 아래와 같은 말풍선이 생기면 클릭하여 놀아주세요 애정도를 많이 얻을 수 있습니다 "
                                            ,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        ,
                                    color = Color.Black
                                )

                                JustImage(
                                    filePath = "etc/loveBubble.json",
                                    modifier = Modifier.size(50.dp),
                                    repetition = true
                                )

                                Text(
                                    text = "또한 펫을 클릭하면 여러 미니 게임을 플레이 할 수 있습니다. 이웃들과 경쟁하여 높은 순위를 차지하고 네임드가 되어보세요!\n\n미션 : 펫을 클릭하여 게임 1회 플레이하기"
                                            ,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 10.dp),
                                    color = Color.Black
                                )

                            }
                        }

                        Spacer(modifier = Modifier.size(12.dp))

                        Row {
                            Spacer(modifier = Modifier.weight(1f))

                            MainButton(
                                onClick = onClose,
                                modifier = Modifier,
                                text = "확인"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun TutorialDialogPreview() {
    MypatTheme {
        TutorialDialog(
            onClose = {},
            state = "펫"
        )
    }
}