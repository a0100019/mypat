package com.a0100019.mypat.presentation.daily.korean

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.index.IndexItemDialog
import com.a0100019.mypat.presentation.index.IndexPatDialog
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
import com.a0100019.mypat.presentation.ui.component.SparkleText
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
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
        koreanText = koreanState.koreanText,
        filter = koreanState.filter,

        onKoreanClick = koreanViewModel::onKoreanClick,
        onFilterClick = koreanViewModel::onFilterClick,
        onCloseClick = koreanViewModel::onCloseClick,
        onStateChangeClick = koreanViewModel::onStateChangeClick,
        onKoreanTextChange = koreanViewModel::onKoreanTextChange,
        onSubmitClick = koreanViewModel::onSubmitClick,
        onFailDialogCloseClick = koreanViewModel::onFailDialogCloseClick
    )
}

@Composable
fun KoreanScreen(
    koreanDataList : List<KoreanIdiom>,
    clickKoreanData : KoreanIdiom?,
    clickKoreanDataState : String,
    koreanText : String,
    filter: String = "일반",

    onKoreanClick : (KoreanIdiom) -> Unit,
    onFilterClick : () -> Unit,
    onCloseClick : () -> Unit,
    onStateChangeClick : () -> Unit,
    onKoreanTextChange : (String) -> Unit,
    onSubmitClick : () -> Unit,
    onFailDialogCloseClick: () -> Unit,
) {

    // 다이얼로그 표시
    if (clickKoreanData != null && clickKoreanDataState == "대기") {
        KoreanReadyDialog(
            koreanData = clickKoreanData,
            onClose = onCloseClick,
            onKoreanTextChange = onKoreanTextChange,
            koreanText = koreanText,
            onSubmitClick = onSubmitClick
        )
    } else if(clickKoreanData != null && clickKoreanDataState in listOf("완료", "별")) {
        KoreanDialog(
            koreanData = clickKoreanData,
            onClose = onCloseClick,
            onStateChangeClick = onStateChangeClick,
            koreanDataState = clickKoreanDataState
        )
    } else if(clickKoreanData != null && clickKoreanDataState == "오답" ) {
        KoreanDialog(
            koreanData = clickKoreanData,
            onClose = onFailDialogCloseClick,
            onStateChangeClick = onStateChangeClick,
            koreanDataState = clickKoreanDataState,
            date = false
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

                if(koreanData.state != "대기" && koreanData.state != "오답"){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                        onClick = { onKoreanClick(koreanData) },
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
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                        onClick = { onKoreanClick(koreanData) },
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
                                text = koreanData.idiom,
                                style = MaterialTheme.typography.titleMedium,
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
            koreanText = "",
            onKoreanTextChange = {},
            onSubmitClick = {},
            onFailDialogCloseClick = {}

        )
    }
}