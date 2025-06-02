package com.a0100019.mypat.presentation.daily.korean

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.image.item.ItemImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun KoreanDialog(
    onClose: () -> Unit,
    koreanData: KoreanIdiom,
    onStateChangeClick: () -> Unit,
    koreanDataState: String,
    date: Boolean = true
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                if(koreanDataState == "완료"){
                    Image(
                        painter = painterResource(id = R.drawable.star_gray),
                        contentDescription = "Sample Vector Image",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                onStateChangeClick()
                            },
                    )
                } else if(koreanDataState == "별"){
                    Image(
                        painter = painterResource(id = R.drawable.star_yellow),
                        contentDescription = "Sample Vector Image",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                onStateChangeClick()
                            },
                    )
                }

                Text(
                    text = koreanData.korean,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                    ,
                    color = Color.Black
                )

                Text(
                    text = koreanData.idiom,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                    ,
                    color = Color.Black
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Text(koreanData.korean1)
                    Text(koreanData.korean2)
                    Text(koreanData.korean3)
                    Text(koreanData.korean4)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = koreanData.meaning,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if(date) {
                    Text(
                        text = "획득 날짜 : ${koreanData.date}",
                        modifier = Modifier
//                        .align(Alignment.End)
                    )
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.End)
//                        .padding(16.dp)
                ) {
                    Text("Close")
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun KoreanDialogPreview() {
    MypatTheme {
        KoreanDialog(
            onClose = {},
            koreanData = KoreanIdiom(),
            onStateChangeClick = {},
            koreanDataState = "완료"
        )
    }
}