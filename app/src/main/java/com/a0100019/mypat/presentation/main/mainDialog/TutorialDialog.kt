package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun TutorialDialog(
    state: String = "미션",
    onStoreClick: () -> Unit = {},
    onDailyClick: () -> Unit = {},
    onPatClick: () -> Unit = {},
    onDesignClick: () -> Unit = {},
    onChatClick: () -> Unit = {}
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
                                .padding(6.dp),
                            color = Color.Black
                        )

                        when(state){
                            "커뮤니티" -> {

                                Text(
                                    text = "(1/3)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.size(12.dp))

                                Text(
                                    text = "하루마을에 오신 것을 환영합니다!\n" +
                                            "\n하루마을은 힐링을 지향하는 작은 세상입니다.\n" +
                                            "게시판과 채팅에서의 따뜻한 말 한마디와 배려가 " +
                                            "하루마을을 더욱 포근하게 채워갑니다.\n" +
                                            "서로를 존중하며 평화로운 하루마을의 역사를 함께 써 내려가요.\n" +
                                            "\n" +
                                            "목표 : 커뮤니티 기능 둘러보기"
                                    ,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {
                                        onChatClick()
                                    },
                                    text = "   커뮤니티   "
                                )

                            }
                            "미션" -> {

                                Text(
                                    text = "(2/3)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.size(12.dp))

                                Text(
                                    text = "하루마을의 에너지원은 바로 관리인의 성실함입니다. " +
                                            "하루 미션 버튼을 눌러 원하는 미션을 완료하고 햇살을 얻어보세요\n\n목표 : 하루 미션 둘러보기",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {
                                        onDailyClick()
                                    },
                                    text = "     하루 미션     "
                                )
                            }
                            "펫" -> {

                                Text(
                                    text = "(3/3)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.size(12.dp))

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
                                    text = "또한 펫을 클릭하면 미니 게임을 통해 펫과 놀아줄 수 있으며, 높은 순위를 차지해보세요! \n\n튜토리얼이 끝났습니다!\n이제부터는 마을의 관리인이 되어 마을의 다양한 기능들을 둘러보고 꾸준히 멋진 마을을 만들어보세요!\n마지막으로 아래의 펫을 눌러주세요"
                                            ,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                                    color = Color.Black
                                )

                                JustImage(
                                    filePath = "pat/cat.json",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                onPatClick()
                                            }
                                        )
                                    ,
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
            state = "펫"
        )
    }
}