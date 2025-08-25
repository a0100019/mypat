package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.image.etc.LoveHorizontalLine
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun PatDialog(
    onClose: () -> Unit,
    patData: Pat,
    patFlowData: Pat?,
    onFirstGameNavigateClick: () -> Unit,
    onSecondGameNavigateClick: () -> Unit,
    onThirdGameNavigateClick: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // 🐾 이름
                Text(
                    text = patData.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer, // 텍스트 강조 색
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 🐶 펫 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            text = "${patFlowData?.love?.div(10000) ?: 0}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        patFlowData?.love?.let { LoveHorizontalLine(it) }
                    }

                    DialogPatImage(patData.url)

                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🎮 미니게임 타이틀
                Text(
                    text = "미니 게임",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer, // 강조 색
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                MainButton(
                    text = "총 게임",
                    onClick = onFirstGameNavigateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )
                MainButton(
                    text = "피하기 게임",
                    onClick = onSecondGameNavigateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )
                MainButton(
                    text = "맞추기 게임",
                    onClick = onThirdGameNavigateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    MainButton(
                        text = "닫기",
                        onClick = onClose,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DialogScreenContentPreview() {
    MypatTheme {
        PatDialog(
            onClose = {},
            patData = Pat(
                url = "pat/flight.json",
                name = "고양이",
                love = 1000,
                memo = "귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다."
            ),
            onFirstGameNavigateClick = {  },
            onSecondGameNavigateClick = {  },
            onThirdGameNavigateClick = {  },
            patFlowData = null
        )
    }
}