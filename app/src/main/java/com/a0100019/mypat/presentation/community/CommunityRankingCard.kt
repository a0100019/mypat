package com.a0100019.mypat.presentation.community

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun CommunityRankingCard(
    userData: AllUser = AllUser(),
    situation: String = "firstGame",
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row {
                Text(
                    text = userData.name
                )

                Text(
                    text = "#" + userData.tag
                )
            }

            when(situation) {
                "firstGame" -> Text(
                    text = userData.firstGame + "점"
                )
                "secondGame" -> Text(
                    text = userData.secondGame + "s"
                )
                "thirdGameEasy" -> Text(
                    text = userData.thirdGameEasy + "개"
                )
                "thirdGameNormal" -> Text(
                    text = userData.thirdGameNormal + "개"
                )
                "thirdGameHard" -> Text(
                    text = userData.thirdGameHard + "개"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityRankingCardPreview() {
    MypatTheme {
        CommunityRankingCard(

            userData = AllUser(
                tag = "436",
                lastLogIn = 342112,
                ban = "0",
                like = "54",
                warning = "0",
                firstDate = "2025-02-05",
                openItem = "30",
                map = "area/forest.jpg",
                name = "이222유빈",
                openPat = "20",
                totalDate = "134",
            ),
        )
    }
}