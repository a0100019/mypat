package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.a0100019.mypat.R
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun TermsDialog(
    onClose: () -> Unit,
    privacy: Boolean = false
) {

    // 상태를 remember로 관리해야 UI가 갱신됨
    var situation by remember { mutableStateOf("이용 약관") }

    if(privacy) situation = "개인정보 처리방침"

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight(0.9f)
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
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {

                Text(
                    text = if(situation == "이용 약관") "이용 약관" else "개인정보 처리방침",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(6.dp)
                        .padding(bottom = 6.dp)
                )

                //약관 이미지 추가

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    MainButton(
                        text = if(situation == "이용 약관") "개인정보 처리방침" else "이용 약관",
                        onClick = {
                            if(situation == "이용 약관") {
                                situation = "개인정보 처리방침"
                            } else {
                                situation = "이용 약관"
                            }
                        },
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    MainButton(
                        text = " 닫기 ",
                        onClick = onClose,
                        modifier = Modifier
                    )
                }

                val context = LocalContext.current
                if(situation == "이용 약관"){
                    val termsText = remember {
                        context.resources.openRawResource(R.raw.terms)
                            .bufferedReader().use { it.readText() }
                    }
                    Text(
                        text = termsText,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                } else {
                    val termsText = remember {
                        context.resources.openRawResource(R.raw.privacy)
                            .bufferedReader().use { it.readText() }
                    }
                    Text(
                        text = termsText,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TermsDialogPreview() {
    MypatTheme {
        TermsDialog(
            onClose = {  },
        )
    }
}