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
    onChatClick: () -> Unit = {},
    onWorldClick: () -> Unit = {},
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
                            "미션" -> {

                                Text(
                                    text = "(1/4)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.size(24.dp))

                                Text(
                                    text = "매일 꾸준히 자기계발 미션을 수행하세요!\n\n여러가지 자기계발 미션들이 준비되어 있으니 " +
                                            "하루 미션 버튼을 눌러 원하는 미션들을 완료하고 햇살을 모아보세요",
                                    style = MaterialTheme.typography.bodyLarge,
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
                            "커뮤니티" -> {

                                Text(
                                    text = "(2/4)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.size(24.dp))

                                Text(
                                    text = "하루마을에는 힐링을 위한 커뮤니티 기능이 있어요.\n" +
                                            "정보를 공유하고, 친구를 만드는 등 자유롭게 커뮤니티 기능을 이용해봐요\n" +
                                            "\n" +
                                            "목표 : 커뮤니티 기능 둘러보기"
                                    ,
                                    style = MaterialTheme.typography.bodyLarge,
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
                            "상점" -> {

                                Text(
                                    text = "(3/4)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.size(24.dp))

                                Text(
                                    text = "상점에서는 마을을 꾸밀 수 있는 펫과 아이템을 구매할 수 있어요.\n" +
                                            "이 외에 다양한 기능들도 구경해봐요\n" +
                                            "\n" +
                                            "목표 : 상점 둘러보기"
                                    ,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {
                                        onStoreClick()
                                    },
                                    text = "   상점   "
                                )

                            }
                            "꾸미기" -> {

                                Text(
                                    text = "(4/4)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.size(24.dp))

                                Text(
                                    text = "마지막으로 나만의 마을을 꾸며봐요\n\n" +
                                            "성장해가는 마을을 보다보면 자기계발 미션에 더욱 의욕이 생길 거에요. 마을에 있는 펫과는 미니게임도 할 수 있어요.\n\n" +
                                            "그럼 이웃님만의 멋진 마을을 기대할게요!\n"
                                    ,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )

                                MainButton(
                                    onClick = {
                                        onWorldClick()
                                    },
                                    text = "   꾸미기   "
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
            state = "꾸미기"
        )
    }
}