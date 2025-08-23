package com.a0100019.mypat.presentation.community

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun CommunityUpdateCheckDialog(
    onConfirmClick: () -> Unit = {},
    onDismissClick : () -> Unit = {}
) {

    Dialog(
        onDismissRequest = onDismissClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .fillMaxHeight(0.8f)
                .shadow(12.dp, shape = RoundedCornerShape(24.dp))
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "인터넷 연결",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.size(30.dp))

                Text(
                    text = "커뮤니티 기능을 이용하기 위해서 인터넷 연결이 필요합니다. 인터넷을 연결한 후 확인 버튼을 눌러주세요",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 6.dp, end = 6.dp)
                )

                Spacer(modifier = Modifier.size(30.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                ){
                    MainButton(
                        text = "  취소  ",
                        onClick = onConfirmClick,
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    MainButton(
                        text = "  확인  ",
                        onClick = onConfirmClick,
                        modifier = Modifier
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CommunityUpdateCheckDialogPreview() {
    MypatTheme {
        CommunityUpdateCheckDialog(
        )
    }
}