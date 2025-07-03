package com.a0100019.mypat.presentation.game.thirdGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.a0100019.mypat.presentation.ui.component.MainButton
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
//                .fillMaxHeight(0.6f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {

                Text(
                    text = "난이도를 설정해주세요",
                    style = MaterialTheme.typography.titleLarge
                )

                MainButton(
                    text = "쉬움",
                    onClick = { onLevelClick(1) },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                MainButton(
                    text = "보통",
                    onClick = { onLevelClick(2) },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                MainButton(
                    text = "어려움",
                    onClick = { onLevelClick(3) },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                MainButton(
                    text = "나가기",
                    onClick = popBackStack,
                    modifier = Modifier
                        .fillMaxWidth()
                )

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