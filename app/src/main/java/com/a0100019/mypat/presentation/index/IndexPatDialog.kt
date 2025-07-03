package com.a0100019.mypat.presentation.index

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.LoveHorizontalLine
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun IndexPatDialog(
    onClose: () -> Unit,
    patData: Pat,
) {
    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
            ) {

                Text(
                    text = patData.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                    ,
                    color = Color.Black
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .fillMaxWidth()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(start = 16.dp, end = 16.dp,)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = "Sample Vector Image",
                            modifier = Modifier.size(20.dp),
                        )
                        Text(" ${patData.love / 100} ")
                        LoveHorizontalLine(patData.love)
                    }
                    DialogPatImage(patData.url)

                }

                Text("획득 날짜 : ${patData.date}")
                Text("애정도 : ${patData.love}")
                Text("같이 플레이 한 게임 수 : ${patData.gameCount}")

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .fillMaxWidth()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = patData.memo,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),

                            )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    MainButton(
                        onClick = onClose,
                        text = " 닫기 "
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun IndexPatDialogPreview() {
    MypatTheme {
        IndexPatDialog(
            onClose = {},
            patData = Pat(
                url = "pat/cat.json",
                name = "고양이",
                love = 1000,
                memo = "귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다."
            ),
        )
    }
}