package com.a0100019.mypat.presentation.game.secondGame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.etc.LoveHorizontalLine
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun SecondGameDialog(
    onClose: () -> Unit,
    time: Double,
    userData: List<User>,
    patData: Pat,
    situation: String,
    popBackStack: () -> Unit,
    plusLove: Int,
) {


    Dialog(
        onDismissRequest = {  }
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .padding(16.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline, // 테두리
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background, // 배경색
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .height(180.dp)
                        .background(
                            MaterialTheme.colorScheme.scrim,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer, // 테두리
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {

                    JustImage(
                        filePath = patData.url
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = "하트",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${patData.love/10000}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        LoveHorizontalLine(
                            value = patData.love,
                            totalValue = 10000,
                            plusValue = plusLove
                        )
                    }

                }

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "시간",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = time.toString() + "초",
                    style = MaterialTheme.typography.displaySmall
                )

                Spacer(modifier = Modifier.size(16.dp))

                if(situation == "신기록") {
                    Text(
                        text = "🎊 신기록 달성!! 🎊",
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
                        text = (userData.find { it.id == "secondGame" }?.value ?: "0") + "초",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .padding(10.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "Sample Vector Image",
                        modifier = Modifier
                            .size(20.dp)
                        ,
                    )

                    Text(
                        text = ", ",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )

                    JustImage(
                        filePath = "etc/moon.png",
                        modifier = Modifier
                            .size(20.dp)
                    )

                    Text(
                        text = " +${plusLove}",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )


                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    MainButton(
                        text = "나가기",
                        onClick = popBackStack,
                        modifier = Modifier
                    )

                    MainButton(
                        text = "다시 하기",
                        onClick = onClose,
                        modifier = Modifier
                    )

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SecondGameDialogPreview() {
    MypatTheme {
        SecondGameDialog(
            onClose = {  },
            time = 190.7,
            userData = listOf(User(id = "curling", value = "10000")),
            patData = Pat(url = "pat/cat.json"),
            situation = "종료",
            popBackStack = {},
            plusLove = 100
        )
    }
}
