package com.a0100019.mypat.presentation.daily.english

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.english.English
import com.a0100019.mypat.presentation.daily.korean.KoreanSideEffect
import com.a0100019.mypat.presentation.daily.korean.KoreanState
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
import com.a0100019.mypat.presentation.ui.component.SparkleText
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun EnglishScreen(
    englishViewModel: EnglishViewModel = hiltViewModel()

) {

    val englishState : EnglishState = englishViewModel.collectAsState().value

    val context = LocalContext.current

    englishViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is EnglishSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    EnglishScreen(

        englishTextList = englishState.englishTextList,
        clickEnglishData = englishState.clickEnglishData,
        filter = englishState.filter,
        clickEnglishDataState = englishState.clickEnglishDataState,
        englishDataList = englishState.englishDataList,
        failEnglishList = englishState.failEnglishList,
        failEnglishStateList = englishState.failEnglishStateList,

        onEnglishClick = englishViewModel::onEnglishClick,
        onAlphabetClick = englishViewModel::onAlphabetClick,
        onAlphabetDeleteClick = englishViewModel::onAlphabetDeleteClick,
        onSubmitClick = englishViewModel::onSubmitClick,
        onFilterClick = englishViewModel::onFilterClick,
        onCloseClick = englishViewModel::onCloseClick,
        onStateChangeClick = englishViewModel::onStateChangeClick

    )
}

@Composable
fun EnglishScreen(

    englishDataList: List<English> = emptyList(),
    clickEnglishData: English? = null,
    filter: String = "일반",
    clickEnglishDataState: String = "",
    englishTextList: List<String> = listOf("", "", "", "", ""),
    failEnglishList: List<String> = emptyList(),
    failEnglishStateList: List<String> = emptyList(),

    onEnglishClick: (English) -> Unit = {},
    onAlphabetClick: (String) -> Unit = {},
    onAlphabetDeleteClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onStateChangeClick: () -> Unit = {}

) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Text in the center
        Text(
            text = "영어 단어",
            style = MaterialTheme.typography.displayMedium, // Large font size
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // 카드 사이 간격 추가
        ) {
            itemsIndexed(englishDataList) { index, englishData ->

                if(englishData.state != "대기"){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                        onClick = { onEnglishClick(englishData) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = englishData.word,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = englishData.meaning,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.weight(1f)) // 텍스트와 이미지 사이 공간 확보

                            val iconRes = if (englishData.state == "완료") {
                                R.drawable.star_gray
                            } else {
                                R.drawable.star_yellow
                            }

                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = "State Icon",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                        onClick = { onEnglishClick(englishData) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "문제를 풀고 보상을 받으세요",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = " " + englishData.date,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.weight(1f)) // 텍스트와 이미지 사이 공간 확보

                            SparkleText(
                                text = "new!!",
                                fontSize = 20
                            )

                        }
                    }

                }

            }
        }

        Row {
            Spacer(modifier = Modifier.weight(1f))
            CuteIconButton(
                onClick = onFilterClick,
                text = " 필터 ",
                imageSize = 20.dp,
                iconResId = if (filter == "일반") R.drawable.star_gray else R.drawable.star_yellow,
                modifier = Modifier
                    .padding(20.dp)
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun EnglishScreenPreview() {
    MypatTheme {
        EnglishScreen(
            englishDataList = listOf(English(state = "대기"), English())
        )
    }
}