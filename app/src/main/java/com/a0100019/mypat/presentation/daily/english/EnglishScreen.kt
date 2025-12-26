package com.a0100019.mypat.presentation.daily.english

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.english.English
import com.a0100019.mypat.presentation.daily.DailySideEffect
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.component.SparkleText
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun EnglishScreen(
    englishViewModel: EnglishViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {}

) {

    val englishState : EnglishState = englishViewModel.collectAsState().value

    val context = LocalContext.current
    val activity = context as Activity

    englishViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is EnglishSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()

            EnglishSideEffect.ShowRewardAd -> {
                englishViewModel.showRewardAd(activity)
            }

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
        notUseEnglishList = englishState.notUseEnglishList,
        useEnglishList = englishState.useEnglishList,
        situation = englishState.situation,

        onEnglishClick = englishViewModel::onEnglishClick,
        onAlphabetClick = englishViewModel::onAlphabetClick,
        onAlphabetDeleteClick = englishViewModel::onAlphabetDeleteClick,
        onSubmitClick = englishViewModel::onSubmitClick,
        onFilterClick = englishViewModel::onFilterClick,
        onCloseClick = englishViewModel::onCloseClick,
        onStateChangeClick = englishViewModel::onStateChangeClick,
        onAdClick = englishViewModel::onAdClick,
        onSituationChange = englishViewModel::onSituationChange,
        popBackStack = popBackStack

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
    notUseEnglishList: List<String> = emptyList(),
    useEnglishList: List<String> = emptyList(),
    situation: String = "",

    onEnglishClick: (English) -> Unit = {},
    onAlphabetClick: (String) -> Unit = {},
    onAlphabetDeleteClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onStateChangeClick: () -> Unit = {},
    popBackStack: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onAdClick: () -> Unit = {},

) {

    when(situation) {
        "hint" -> SimpleAlertDialog(
            onConfirmClick = {
                onAdClick()
            },
            onDismissClick = {
                onSituationChange("")
            },
            text = "광고를 보고 영어 단어의 뜻을 보겠습니까?",
        )
    }

    if(clickEnglishData != null && clickEnglishDataState in listOf("대기", "뜻")) {
        EnglishReadyDialog(
            englishTextList = englishTextList,
            failEnglishList = failEnglishList,
            failEnglishStateList = failEnglishStateList,
            onClose = onCloseClick,
            onAlphabetClick = onAlphabetClick,
            onSubmitClick = onSubmitClick,
            onAlphabetDeleteClick = onAlphabetDeleteClick,
            notUseEnglishList = notUseEnglishList,
            useEnglishList = useEnglishList,
            clickEnglishDataState = clickEnglishDataState,
            clickEnglishData = clickEnglishData,
            onHintClick = {
                onSituationChange("hint")
            }
        )
    } else if(clickEnglishData != null && clickEnglishDataState in listOf("완료", "별")) {
        EnglishDialog(
            onClose = onCloseClick,
            english = clickEnglishData,
            onStateChangeClick = onStateChangeClick,
            englishDataState = clickEnglishDataState
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Text in the center
                Text(
                    text = "영어 단어",
                    style = MaterialTheme.typography.displayMedium, // Large font size
                    modifier = Modifier
                )

                // 오른쪽 버튼
                MainButton(
                    text = "닫기",
                    onClick = popBackStack,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp) // 카드 사이 간격 추가
            ) {
                itemsIndexed(englishDataList) { index, englishData ->

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    if (englishData.state !in listOf("대기", "뜻")) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onEnglishClick(englishData) }
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
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
                                // 영어 단어
                                Text(
                                    text = englishData.word,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                // 뜻
                                Text(
                                    text = englishData.meaning,
                                    style = MaterialTheme.typography.bodyLarge,
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                // 상태 아이콘
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
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onEnglishClick(englishData) }
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.scrim
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
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "📅 ${englishData.date}",
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }

                                SparkleText(
                                    text = "NEW!!",
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