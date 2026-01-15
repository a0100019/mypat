package com.a0100019.mypat.presentation.activity.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.MusicPlayer
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun RoomUpDialog(
    onClose: () -> Unit,
    userData: List<User>,
    showRoomUpDialog: String
) {

    MusicPlayer(
        id = R.raw.positive2
    )

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(showRoomUpDialog == "pat") {
                    Text(
                        text = "펫 공간이 (${userData.find { it.id == "pat" }?.value2!!.toInt()-1} -> ${userData.find { it.id == "pat" }?.value2}) 로 증가하였습니다!",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Text(
                        text = "최대 5칸",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(32.dp)
                    )

                } else {

                    Text(
                        text = "아이템 공간이 (${userData.find { it.id == "item" }?.value2!!.toInt()-1} -> ${userData.find { it.id == "item" }?.value2}) 로 증가하였습니다!!",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Text(
                        text = "최대 10칸",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(32.dp)
                    )

                }

                MainButton(
                    text = " 닫기 ",
                    onClick = onClose,
                    modifier = Modifier
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