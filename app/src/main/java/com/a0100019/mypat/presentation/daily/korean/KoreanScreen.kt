package com.a0100019.mypat.presentation.daily.korean

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.component.SparkleText
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun KoreanScreen(
    koreanViewModel: KoreanViewModel = hiltViewModel()

) {

    val koreanState : KoreanState = koreanViewModel.collectAsState().value

    val context = LocalContext.current

    koreanViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is KoreanSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    KoreanScreen(
        koreanDataList = koreanState.koreanDataList,
        clickKoreanData = koreanState.clickKoreanData,
        clickKoreanDataState = koreanState.clickKoreanDataState,
        filter = koreanState.filter,
        koreanCharacter1 = koreanState.koreanCharacter1,
        koreanCharacter2 = koreanState.koreanCharacter2,
        koreanCharacter3 = koreanState.koreanCharacter3,
        koreanCharacter4 = koreanState.koreanCharacter4,
        informationText = koreanState.informationText,
        koreanCharacterList = koreanState.koreanCharacterList,

        onKoreanClick = koreanViewModel::onKoreanClick,
        onFilterClick = koreanViewModel::onFilterClick,
        onCloseClick = koreanViewModel::onCloseClick,
        onStateChangeClick = koreanViewModel::onStateChangeClick,
        onSubmitClick = koreanViewModel::onSubmitClick,
        onKoreanDeleteClick = koreanViewModel::onKoreanDeleteClick,
        onKoreanCharacterClick = koreanViewModel::onKoreanCharacterClick
    )
}

@Composable
fun KoreanScreen(
    koreanDataList : List<KoreanIdiom>,
    clickKoreanData : KoreanIdiom?,
    clickKoreanDataState : String,
    filter: String = "일반",
    koreanCharacter1: String = "",
    koreanCharacter2: String = "",
    koreanCharacter3: String = "",
    koreanCharacter4: String = "",
    koreanCharacterList: List<String> = emptyList(),
    informationText: String = "",

    onKoreanClick : (KoreanIdiom) -> Unit,
    onFilterClick : () -> Unit,
    onCloseClick : () -> Unit,
    onStateChangeClick : () -> Unit,
    onSubmitClick : () -> Unit,
    onKoreanCharacterClick: (String) -> Unit = {},
    onKoreanDeleteClick: () -> Unit = {},


) {

    // 다이얼로그 표시
    if (clickKoreanData != null && clickKoreanDataState == "대기") {
        KoreanReadyDialog(
            koreanData = clickKoreanData,
            onClose = onCloseClick,
            onSubmitClick = onSubmitClick,
            koreanCharacter1 = koreanCharacter1,
            koreanCharacter2 = koreanCharacter2,
            koreanCharacter3 = koreanCharacter3,
            koreanCharacter4 = koreanCharacter4,
            koreanCharacterList = koreanCharacterList,
            informationText = informationText,
            onKoreanCharacterClick = onKoreanCharacterClick,
            onKoreanDeleteClick = onKoreanDeleteClick,

        )
    } else if(clickKoreanData != null && clickKoreanDataState in listOf("완료", "별")) {
        KoreanDialog(
            koreanData = clickKoreanData,
            onClose = onCloseClick,
            onStateChangeClick = onStateChangeClick,
            koreanDataState = clickKoreanDataState
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        
        // Text in the center
        Text(
            text = "사자성어",
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
            itemsIndexed(koreanDataList) { index, koreanData ->

                if(koreanData.state != "대기"){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .shadow(8.dp, RoundedCornerShape(16.dp)), // 더 부드럽고 깊은 그림자
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                        onClick = { onKoreanClick(koreanData) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.scrim
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = koreanData.idiom,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = koreanData.korean,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.weight(1f)) // 텍스트와 이미지 사이 공간 확보

                            val iconRes = if (koreanData.state == "완료") {
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
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .shadow(8.dp, RoundedCornerShape(16.dp)), // 더 부드럽고 깊은 그림자
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                        onClick = { onKoreanClick(koreanData) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.scrim // 더 강조된 배경색
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "문제를 풀고 보상을 받으세요!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "📅 ${koreanData.date}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            SparkleText(
                                text = "✨NEW✨",
                                fontSize = 20,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }


                }

            }
        }

        Row {
            Spacer(modifier = Modifier.weight(1f))
            MainButton(
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
fun KoreanScreenPreview() {
    MypatTheme {
        KoreanScreen(
            koreanDataList = listOf(KoreanIdiom(state = "대기"), KoreanIdiom()),
            clickKoreanData = KoreanIdiom(),
            onKoreanClick = {},
            onFilterClick = {},
            onCloseClick = {},
            onStateChangeClick = {},
            clickKoreanDataState = "",
            onSubmitClick = {},


        )
    }
}