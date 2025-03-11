package com.a0100019.mypat.presentation.game.secondGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.image.etc.LoveHorizontalLine
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun SecondGameOverDialog(
    onClose: () -> Unit,
    time: Double,
    userData: List<User>,
    patData: Pat,
    situation: String,
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
                        plusValue = time.toInt()
                    )
                }

                Text("시간")
                Text(text = time.toString())
                if(situation == "신기록") {
                    Text("신기록 달성!!")
                } else {
                    Text(text = "최고 기록")
                    Text(text = userData.find { it.id == "secondGame" }?.value ?: "")
                }


                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp)
                ) {
                    Text("다시 하기")
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameOverDialogDialogPreview() {
    MypatTheme {
        SecondGameOverDialog(
            onClose = {  },
            time = 190.7,
            userData = listOf(User(id = "curling", value = "10000")),
            patData = Pat(url = "pat/cat.json"),
            situation = "종료"
        )
    }
}
