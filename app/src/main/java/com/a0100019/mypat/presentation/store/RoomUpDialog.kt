package com.a0100019.mypat.presentation.store

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun RoomUpDialog(
    onClose: () -> Unit,
    userData: List<User>,
    showRoomUpDialog: String
) {
    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(showRoomUpDialog == "pat") {
                    Text(
                        text = "펫 공간이 (${userData.find { it.id == "pat" }?.value2!!.toInt()-1} -> ${userData.find { it.id == "pat" }?.value2}) 로 증가하였습니다!\n최대 10칸",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "아이템 공간이 (${userData.find { it.id == "item" }?.value2!!.toInt()-1} -> ${userData.find { it.id == "item" }?.value2}) 로 증가하였습니다!!\n최대 10칸",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                MainButton(
                    text = " 닫기 ",
                    onClick = onClose,
                    modifier = Modifier
                        .padding(top = 16.dp)
                )

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PatRoomUpDialogPreview() {
    MypatTheme {
        RoomUpDialog(
            onClose = {},
            userData = listOf(),
            showRoomUpDialog = "pat"
        )
    }
}