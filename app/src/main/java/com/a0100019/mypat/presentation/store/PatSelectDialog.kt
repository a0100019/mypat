package com.a0100019.mypat.presentation.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun PatSelectDialog(
    onAdvertisementClick: () -> Unit,
    onSelectClick: () -> Unit,
    patData: Pat,
) {
    Dialog(
        onDismissRequest = {  }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Text(
                    text = "선택하시겠습니까?"
                )

                Box {
                    JustImage(
                        filePath = patData.url,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Text(
                    text = patData.name
                )

                Row {
                    Button(
                        onClick = onAdvertisementClick,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text("광고보기")
                    }

                    Button(
                        onClick = onSelectClick,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text("선택하기")
                    }

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PatSelectDialogPreview() {
    MypatTheme {
        PatSelectDialog(
            onAdvertisementClick = {},
            onSelectClick = {},
            patData = Pat(name = "고양이", url = "pat/cat.json")
        )
    }
}