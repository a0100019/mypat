package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun CouponDialog(
    onClose: () -> Unit,
    onCouponTextChange: (String) -> Unit,
    couponText: String,
    onConfirmClick: () -> Unit,
) {

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
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "쿠폰 코드를 입력해주세요.",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(10.dp),
                )

                OutlinedTextField(
                    value = couponText,
                    onValueChange = onCouponTextChange,
                    label = { Text("쿠폰 코드") },
                    placeholder = { Text("쿠폰 코드를 입력해주세요.") },
                    singleLine = true,
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = Color.Blue,
//                unfocusedBorderColor = Color.Gray
//            ),
                    shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )


                Spacer(modifier = Modifier.height(16.dp))

                // 추가로 원하는 Composable 요소

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MainButton(
                        text = " 취소 ",
                        onClick = onClose,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    MainButton(
                        text = " 확인 ",
                        onClick = onConfirmClick,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CouponDialogPreview() {
    MypatTheme {
        CouponDialog(
            onClose = {},
            onCouponTextChange = {},
            onConfirmClick = {},
            couponText = "",
        )
    }
}