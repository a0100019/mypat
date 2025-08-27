package com.a0100019.mypat.presentation.game.thirdGame

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

@SuppressLint("DefaultLocale")
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
                    JustImage(
                        filePath = patData.url
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when(level) {
                        1 -> "쉬움 난이도 클리어!"
                        2 -> "보통 난이도 클리어!"
                        else -> "어려움 난이도 클리어!"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(16.dp)
                )

                Text(
                    text = "시간",
                    style = MaterialTheme.typography.titleMedium
                    )

                val minutes = (time / 60).toInt()
                val seconds = time % 60

                Text(
                    text = String.format("%d분 %.2f초", minutes, seconds),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "총 클리어 개수 : " +
                        when(level) {
                            1 -> "${(userData.find { it.id == "thirdGame" }!!.value).toInt()+1}"
                            2 -> "${(userData.find { it.id == "thirdGame" }!!.value2).toInt()+1}"
                            else -> "${(userData.find { it.id == "thirdGame" }!!.value3).toInt()+1}"
                        } +
                        "개"
                    ,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "애정도, 달빛 +$plusLove"
                )

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
