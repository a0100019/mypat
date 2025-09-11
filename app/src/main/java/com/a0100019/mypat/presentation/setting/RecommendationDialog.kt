package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun RecommendationDialog(
    onClose: () -> Unit = {},
    onRecommendationTextChange: (String) -> Unit = {},
    recommendationText: String = "1",
    recommending: String = "-1",
    recommended: String = "-1",
    userData: List<User> = emptyList(),
    onRecommendationSubmitClick: () -> Unit = {},
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
//                .fillMaxHeight(0.5f)
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

                if(recommending != "-1"){
                    Text(
                        text = "추천 하기",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(10.dp),
                    )

                    if(recommending == "0"){
                        Text(
                            text = "하루마을을 소개해 준 친구 혹은 추천하고 싶은 이웃의 태그를 입력하세요. 5 햇살을 받을 수 있습니다. 단, 서로를 추천할 수 없으며 오직 1회만 가능하니 신중하게 입력해주세요.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp),
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "#",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(end = 3.dp)
                            )

                            OutlinedTextField(
                                value = recommendationText,
                                onValueChange = { newValue ->
                                    // 숫자만 허용
                                    if (newValue.all { it.isDigit() }) {
                                        onRecommendationTextChange(newValue)
                                    }
                                },
                                label = { Text("추천인 태그") },
                                placeholder = {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = "태그 입력 ( # 뒤의 숫자 )"
                                    )
                                },

                                shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // 숫자 키보드
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .weight(1f)
                            )

                            MainButton(
                                text = " 입력 ",
                                onClick = onRecommendationSubmitClick,
                                modifier = Modifier
                            )

                        }
                    } else {

                        Text(
                            text = "#$recommending 님을 추천하였습니다."
                        )

                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "추천 받기",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(10.dp),
                        )

                    if(recommended == "0"){

                        val myTag = userData.find { it.id == "auth" }!!.value2
                        Text(
                            text = "아직 추천 받지 못했습니다. 친구를 초대하여 추천을 받으면 10 햇살을 받습니다. 단, 추천 보상은 한번만 받을 수 있습니다.\n나의 태그 : #$myTag",
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "#$recommended 님의 추천을 받았습니다."
                        )
                    }

                } else {
                    Text(
                        "인터넷 오류"
                    )
                }

                MainButton(
                    text = " 확인 ",
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
fun RecommendationDialogPreview() {
    MypatTheme {
        RecommendationDialog(
            onClose = {},
            onRecommendationTextChange = {},
            recommending = "1"
        )
    }
}