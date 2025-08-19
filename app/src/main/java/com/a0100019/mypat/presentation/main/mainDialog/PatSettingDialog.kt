package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.component.MainButton
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
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline, // 테두리
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background, // 배경색
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
                    text = patData.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "효과를 선택해주세요"
                )

                Row {
                    repeat(6) { index ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if(index == 0) {
                                Image(
                                    painter = painterResource(id = R.drawable.cancel),
                                    contentDescription = null,
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
                            } else {
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

                            Text(
                                text = when(index) {
                                    1 -> "1"
                                    2 -> "3"
                                    3 -> "10"
                                    4 -> "25"
                                    5 -> "50"
                                    else -> ""
                                }
                            )
                        }

                    }

                }

                Text(
                    text = "현재 애정도 레벨 : ${patData.love/10000}"
                    ,
                    modifier = Modifier
                        .padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.size(16.dp))

                val sizeRatio = when (patData.sizeFloat/patData.minFloat) {
                    in 0.9..1.1 -> "1"
                    in 1.15..1.35 -> "1.25"
                    in 1.4..1.6 -> "1.5"
                    in 1.65..1.85 -> "1.75"
                    in 1.9..2.1 -> "2"
                    else -> "??"
                }
                Text(
                    text = "크기 X $sizeRatio",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    MainButton(
                        text = " 지우기 ",
                        onClick = onDelete,
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    MainButton(
                        text = " 줄이기 ",
                        onClick = onSizeDown,
                        modifier = Modifier
                            .padding(end = 12.dp)
                    )

                    MainButton(
                        text = " 키우기 ",
                        onClick = onSizeUp,
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
