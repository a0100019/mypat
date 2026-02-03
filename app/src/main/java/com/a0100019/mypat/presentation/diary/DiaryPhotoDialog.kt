package com.a0100019.mypat.presentation.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryPhotoDialog(
    onClose: () -> Unit,
    clickPhoto: String = "",
) {

    Dialog(
        onDismissRequest = onClose
    ) {

        Column(horizontalAlignment = Alignment.End) {

            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .clickable {
                        onClose()
                    }
            ) {

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.End
                ) {

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        // 로컬 경로에 있는 이미지를 불러옵니다.
                        AsyncImage(
                            model = clickPhoto, // 파일 경로를 그대로 넣으면 됩니다
                            contentDescription = "일기 사진",
                            modifier = Modifier,
                            contentScale = ContentScale.Inside
                        )

                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryPhotoDialogPreview() {
    MypatTheme {
        DiaryPhotoDialog(
            onClose = {},
        )
    }
}