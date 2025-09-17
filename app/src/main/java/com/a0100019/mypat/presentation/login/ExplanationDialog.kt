package com.a0100019.mypat.presentation.login

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import kotlin.random.Random

@Composable
fun ExplanationDialog(
    onClose: () -> Unit,
) {

    Dialog(
        onDismissRequest = {  }
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(340.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
//                    .fillMaxWidth(0.8f)
            ) {

                //편지 이미지
                JustImage(
                    filePath = "etc/letter.png",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                )

                Row {
                    Spacer(modifier = Modifier.weight(1f))

                    Column (
                        modifier = Modifier
                            .weight(8f)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .weight(10f)
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text(
                                text = "스토리",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = "아주 오래 전, 하루마을은 모두가 행복하게 지내는 따뜻한 마을이었습니다. 하지만 마을을 돌보던 지혜로운 관리인이 병으로 세상을 떠난 뒤, 모든 게 멈춰 버렸습니다. 돌보는 손길이 사라지자, 펫들은 흩어졌고 잿빛 그림자만이 이 마을을 덮고 있었습니다\n\n" +
                                "하지만 마지막 희망이 남아있었습니다\n\n" +
                                "생기를 잃어가는 마을과 불쌍한 펫들을 발견한 당신은, 새로운 관리인이 되어 마을을 살리기로 결심하였습니다",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = "설명서",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 10.dp),
                                color = Color.Black
                            )

                            Text(
                                text = "1.  하루마을의 에너지원은 바로 관리인의 성실함입니다. 하루마을에는 총 4가지 하루 미션이 있습니다. " +
                                "게임을 시작하면 아래 버튼을 가장 먼저 눌러 미션들을 완료하고 주된 화폐인 햇살을 얻어주세요",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 10.dp),
                                color = Color.Black
                            )

                            MainButton(
                                onClick = {},
                                text = "     하루 미션     "
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = "2.  펫과 놀아주세요! 10분마다 펫 머리 위에 말풍선이 생기면 클릭하여 놀아주세요 애정도와 달빛을 얻을 수 있습니다. " +
                                "또한 펫을 클릭하면 ３가지 게임을 플레이 할 수 있으며 이웃들과 경쟁하여 높은 순위를 차지해보세요",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp),
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = "3.  마을을 꾸며보세요! ",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp),
                                color = Color.Black
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                            ) {

                                MainButton(
                                    onClick = onClose,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    text = " 확인 "
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
                text = "아래로 드래그하세요",
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
fun ExplanationDialogPreview() {
    MypatTheme {
        ExplanationDialog(
            onClose = {},
        )
    }
}