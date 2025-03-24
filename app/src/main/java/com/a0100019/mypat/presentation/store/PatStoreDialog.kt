package com.a0100019.mypat.presentation.store

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun PatStoreDialog(
    onClose: () -> Unit,
    patData: List<Pat>,
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Text(text = "펫 뽑기")
                Row {
                    repeat(5) {
                        DialogPatImage(
                            patUrl = patData[it].url,
                            modifier = Modifier
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column() {



                    }
                }

//                Button(
//                    onClick = onClose,
//                    modifier = Modifier
//                        .align(Alignment.End)
//                        .padding(16.dp)
//                ) {
//                    Text("다시 하기")
//                }

            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun PatStoreDialogPreview() {
    MypatTheme {
        PatStoreDialog(
            onClose = {},
            patData = listOf(Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json")),
        )
    }
}