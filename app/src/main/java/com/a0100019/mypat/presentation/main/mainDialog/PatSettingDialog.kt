package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.etc.patEffectIndexToUrl
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun PatSettingDialog(
    onDelete: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onSizeUp: () -> Unit = {},
    onSizeDown: () -> Unit = {},
    onPatEffectChangeClick: (Int) -> Unit = {},
    patData: Pat,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = patData.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text("${patData.name}에 대한 크기 조정 및 삭제를 수행할 수 있습니다.\n 현재 크기 : ${patData.sizeFloat.toDouble()}")

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "효과를 선택해주세요"
                )

                Row {
                    repeat(5) { index ->
                        JustImage(
                            filePath = patEffectIndexToUrl(index),
                            modifier = Modifier
                                .size(30.dp)
                                .then(
                                    if (patData.effect == index)
                                        Modifier.border(
                                            width = 2.dp,
                                            color = Color.DarkGray,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                    else Modifier
                                )
                                .clickable { onPatEffectChangeClick(index) }
                        )
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                Row {

                    CuteIconButton(
                        text = "키우기",
                        onClick = onSizeUp,
                        modifier = Modifier
                    )

                    CuteIconButton(
                        text = "줄이기",
                        onClick = onSizeDown,
                        modifier = Modifier
                    )

                    CuteIconButton(
                        text = "지우기",
                        onClick = onDelete,
                        modifier = Modifier
                    )

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PatSettingDialogPreview() {
    MypatTheme {
        PatSettingDialog(
            patData = Pat(name = "고양이", url = "pat/cat.json")
        )
    }
}
