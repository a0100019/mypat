package com.a0100019.mypat.presentation.game.thirdGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.a0100019.mypat.presentation.ui.image.etc.LoveHorizontalLine
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun ThirdGameStartDialog(
    patData: Pat,
    popBackStack: () -> Unit,
    onLevelClick: (Int) -> Unit,
) {

    Dialog(
        onDismissRequest = {  }
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
                    )
                }

                Text(
                    text = "난이도를 설정해주세요"
                )

                Button(
                    onClick = { onLevelClick(1) },
                    modifier = Modifier
                ) {
                    Text("쉬움")
                }

                Button(
                    onClick = { onLevelClick(2) },
                    modifier = Modifier
                ) {
                    Text("보통")
                }

                Button(
                    onClick = { onLevelClick(3) },
                    modifier = Modifier
                ) {
                    Text("어려움")
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

@Preview(showBackground = true)
@Composable
fun ThirdGameStartDialogPreview() {
    MypatTheme {
        ThirdGameStartDialog(
            patData = Pat(url = "pat/cat.json"),
            onLevelClick = {},
            popBackStack = {},
        )
    }
}