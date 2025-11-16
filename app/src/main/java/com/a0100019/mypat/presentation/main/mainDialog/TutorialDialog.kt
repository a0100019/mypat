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
                            "미션" -> {
                                Text(
                                    text = "하루마을의 에너지원은 바로 관리인의 성실함입니다. 하루마을에는 현재 총 4가지의 간단한 하루 미션이 있습니다. " +
                                            "하루 미션 버튼을 눌러 원하는 미션들을 완료하고 주된 화폐인 햇살을 얻어주세요\n\n목표 : 하루 미션 한 개 완료하기",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {},
                                    text = "     하루 미션     "
                                )
                            }
                            "상점" -> {
                                Text(
                                    text = "상점에서 햇살로 펫을 뽑고 달빛으로 그 외 다양한 아이템을 살 수 있습니다. 햇살은 하루 미션으로 얻을 수 있고, 달빛은 주로 미니 게임을 통해 얻을 수 있으며 " +
                                            "햇살은 달빛으로 교환할 수 있습니다\n\n목표 : 상점 이용하기",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {},
                                    text = "     상점     "
                                )
                            }
                            "꾸미기" -> {
                                Text(
                                    text = "꾸미기 모드에서 원하는 펫과 아이템을 배치하고 맵을 바꿀 수 있습니다. 펫을 클릭하여 크기를 조정할 수 있으며 레벨에 따라 효과를 적용할 수 있습니다. 나만의 멋진 마을을 만들어보세요!\n\n목표 : 꾸미기 완료하기",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {},
                                    text = "    꾸미기 모드    "
                                )
                            }
                            "커뮤니티" -> {

                                Text(
                                    text = "커뮤니티 기능을 이용해보세요! 다른 사람들의 마을을 구경하고 좋아요를 눌러보세요. 채팅으로 대화도 할 수 있습니다. 개성있는 나만의 마을도 꾸미고 미니 게임에서 높은 순위를 차지해서 하루마을 명예의 전당에 이름을 남겨보세요!\n" +
                                            "\n" +
                                            "목표 : 커뮤니티 기능 둘러보기"
                                    ,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {},
                                    text = "     커뮤니티     "
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
                                    text = "또한 펫을 클릭하면 미니 게임을 통해 펫과 놀아줄 수 있고 애정도와 달빛을 얻을 수 있습니다. \n\n튜토리얼이 끝났습니다! 이제부터는 하루 마을의 주인이 되어 꾸준히 멋진 마을을 만들어가 보세요! 마지막으로 아래의 펫을 눌러주세요"
                                            ,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 10.dp),
                                    color = Color.Black
                                )

                                JustImage(
                                    filePath = "pat/cat.json",
                                    modifier = Modifier.size(50.dp),
                                    repetition = true
                                )

                            }
                        }

                        Spacer(modifier = Modifier.size(12.dp))
//
//                        Row {
//                            Spacer(modifier = Modifier.weight(1f))
//
//                            MainButton(
//                                onClick = onClose,
//                                modifier = Modifier,
//                                text = "확인"
//                            )
//                        }
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