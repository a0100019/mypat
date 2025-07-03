package com.a0100019.mypat.presentation.game.firstGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.LoveHorizontalLine
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme



@Composable
fun FirstGameOverDialog(
    onClose: () -> Unit,
    popBackStack: () -> Unit,
    score: Int,
    level: Int,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

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
                        plusValue = score
                    )
                }

                Text(
                    text = "점수",
                    style = MaterialTheme.typography.titleMedium
                    )
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.displayMedium
                )
                Text("레벨",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = level.toString(),
                    style = MaterialTheme.typography.headlineMedium
                )
                if(situation == "신기록") {
                    Text(
                        text = "신기록 달성!!",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(30.dp)
                        )
                } else {
                    Text(
                        text = "최고 기록",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = userData.find { it.id == "firstGame" }?.value ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    MainButton(
                        text = "다시 하기",
                        onClick = onClose,
                        modifier = Modifier
                    )

                    MainButton(
                        text = "나가기",
                        onClick = popBackStack,
                        modifier = Modifier
                    )

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameOverDialogDialogPreview() {
    MypatTheme {
        FirstGameOverDialog(
            onClose = {  },
            level = 3,
            score = 190,
            userData = listOf(User(id = "firstGame", value = "10000")),
            patData = Pat(url = "pat/cat.json"),
            situation = "종료",
            popBackStack = {}
        )
    }
}