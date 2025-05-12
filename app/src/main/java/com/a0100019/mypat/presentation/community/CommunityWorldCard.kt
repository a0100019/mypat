package com.a0100019.mypat.presentation.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun CommunityWorldCard(
    userData: AllUser = AllUser(),
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // 가로 크기는 최대
                    .aspectRatio(1 / 1.25f), // 세로가 가로의 1.25배
            ) {
               Text("aa")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "커뮤니티 월드 카드",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CommunityWorldCardPreview() {
    MypatTheme {
        CommunityWorldCard(

            userData = AllUser(
                tag = "436",
                lastLogIn = 342112,
                ban = "0",
                like = "54",
                warning = "0",
                firstDate = "2025-02-05",
                openItem = "30",
                openItemSpace = "10",
                map = "map/forest.jpg",
                name = "이222유빈",
                openPat = "20",
                openPatSpace = "10",
                totalDate = "134",
                worldData0 = "1@0.2@pat@0.25@0.69",
                worldData1 = "2@0.2@pat@0.25@0.569",
                worldData2 = "1@0.2@pat@0.125@0.69",
                worldData3 = "1@0.2@item@0.25@0.69",
                worldData4 = "2@0.2@item@0.125@0.769",
                worldData5 = "1@0.2@item@0.225@0.169",
                worldData6 = "1@0.2@pat@0.25@0.669",
                worldData7 = "2@0.2@pat@0.25@0.369",
                worldData8 = "2@0.3@pat@0.325@0.69",
                worldData9 = "1@0.2@pat@0.725@0.769",
                worldData10 = "1@0.2@item@0.425@0.669",
            ),
        )
    }
}