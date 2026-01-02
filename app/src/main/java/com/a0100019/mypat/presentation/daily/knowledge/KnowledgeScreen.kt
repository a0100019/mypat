package com.a0100019.mypat.presentation.daily.knowledge

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.knowledge.Knowledge
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.daily.korean.KoreanDialog
import com.a0100019.mypat.presentation.daily.korean.KoreanReadyDialog
import com.a0100019.mypat.presentation.daily.korean.KoreanSideEffect
import com.a0100019.mypat.presentation.daily.korean.KoreanState
import com.a0100019.mypat.presentation.daily.korean.KoreanViewModel
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.component.SparkleText
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun KnowledgeScreen(
    knowledgeViewModel: KnowledgeViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {}

) {

    val knowledgeState : KnowledgeState = knowledgeViewModel.collectAsState().value

    val context = LocalContext.current

    knowledgeViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is KnowledgeSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    KnowledgeScreen(
        knowledgeDataList = knowledgeState.knowledgeDataList,
        clickKnowledgeData = knowledgeState.clickKnowledgeData,
        clickKnowledgeDataState = knowledgeState.clickKnowledgeDataState,
        filter = knowledgeState.filter,
        text = knowledgeState.text,

        onKnowledgeClick = knowledgeViewModel::onKnowledgeClick,
        onFilterClick = knowledgeViewModel::onFilterClick,
        onCloseClick = knowledgeViewModel::onCloseClick,
        onStateChangeClick = knowledgeViewModel::onStateChangeClick,
        onSubmitClick = knowledgeViewModel::onSubmitClick,
        onTextChange = knowledgeViewModel::onTextChange,
        popBackStack = popBackStack
    )
}

@Composable
fun KnowledgeScreen(
    knowledgeDataList : List<Knowledge>,
    clickKnowledgeData : Knowledge?,
    clickKnowledgeDataState : String = "",
    filter: String = "일반",
    text: String = "",

    onKnowledgeClick : (Knowledge) -> Unit = {},
    onFilterClick : () -> Unit,
    onCloseClick : () -> Unit,
    onStateChangeClick : () -> Unit = {},
    onSubmitClick : () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    popBackStack: () -> Unit = {},

    ) {

    // 다이얼로그 표시
    if (clickKnowledgeData != null && clickKnowledgeDataState == "대기") {
        KnowledgeReadyDialog(
            onClose = onCloseClick,
            knowledgeData = clickKnowledgeData,
            onTextChange = onTextChange,
            onSubmitClick = onSubmitClick,
            text = text
        )
    } else if (clickKnowledgeData != null && clickKnowledgeDataState in listOf("완료", "별")) {
        KnowledgeDialog(
            onClose = onCloseClick,
            knowledgeData = clickKnowledgeData,
            onStateChangeClick = onStateChangeClick,
            knowledgeDataState = clickKnowledgeDataState
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
                    text = "상식",
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
                itemsIndexed(knowledgeDataList) { index, knowledgeData ->

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    if (knowledgeData.state != "대기") {
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
                                    onClick = { onKnowledgeClick(knowledgeData) }
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

                                Text(
                                    text = knowledgeData.answer,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.size(10.dp))

                                Spacer(modifier = Modifier.weight(1f)) // 텍스트와 이미지 사이 공간 확보

                                val iconRes = if (knowledgeData.state == "완료") {
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
                                    onClick = { onKnowledgeClick(knowledgeData) }
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
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
                                        text = "오늘의 상식을 확인하세요!",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "📅 ${knowledgeData.date}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun KnowledgeScreenPreview() {
    MypatTheme {
        KnowledgeScreen(


            )
    }
}