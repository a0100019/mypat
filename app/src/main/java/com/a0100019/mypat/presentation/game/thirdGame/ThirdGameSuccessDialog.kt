package com.a0100019.mypat.presentation.game.thirdGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.image.etc.LoveHorizontalLine
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun ThirdGameSuccessDialog(
    onClose: () -> Unit,
    time: Double,
    userData: List<User>,
    patData: Pat,
    popBackStack: () -> Unit,
    plusLove: Int,
    level: Int,
) {


    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    DialogPatImage(patData.url)
                    LoveHorizontalLine(
                        value = patData.love,
                        totalValue = 10000,
                        plusValue = plusLove,
                    )
                }


                Text("시간")
                Text(text = time.toString())
                Text(
                    text = when(level) {
                        1 -> "쉬움 클리어!"
                        2 -> "보통 클리어!"
                        else -> "어려움 클리어!"
                    }
                )
                Text("총 클리어 개수 : ${(userData.find { it.id == "thirdGame" }!!.value).toInt()+1}개")
                Button(
                    onClick = { },
                    modifier = Modifier
                ) {
                    Text("전체 순위 보기")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Button(
                        onClick = onClose,
                        modifier = Modifier
                    ) {
                        Text("다시 하기")
                    }

                    Button(
                        onClick = popBackStack,
                        modifier = Modifier
                    ) {
                        Text("나가기")
                    }

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ThirdGameDialogPreview() {
    MypatTheme {
        ThirdGameSuccessDialog(
            onClose = {  },
            time = 190.7,
            userData = listOf(User(id = "thirdGame", value = "10000")),
            patData = Pat(url = "pat/cat.json"),
            popBackStack = {},
            plusLove = 100,
            level = 1
        )
    }
}
