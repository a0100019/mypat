package com.a0100019.mypat.presentation.daily.korean

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.index.IndexItemDialog
import com.a0100019.mypat.presentation.index.IndexPatDialog
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

    onKoreanClick : (KoreanIdiom) -> Unit,
    onFilterClick : () -> Unit,
    onCloseClick : () -> Unit,
    onStateChangeClick : () -> Unit,
    onKoreanTextChange : (String) -> Unit,
    onSubmitClick : () -> Unit,
    onFailDialogCloseClick: () -> Unit,
) {

    // 다이얼로그 표시
    if (clickKoreanData != null && clickKoreanData.state == "대기") {
        KoreanReadyDialog(
            koreanData = clickKoreanData,
            onClose = onCloseClick,
            onKoreanTextChange = onKoreanTextChange,
            koreanText = koreanText,
            onSubmitClick = onSubmitClick
        )
    } else if(clickKoreanData != null && clickKoreanData.state in listOf("완료", "별")) {
        KoreanDialog(
            koreanData = clickKoreanData,
            onClose = onCloseClick,
            onStateChangeClick = onStateChangeClick,
            koreanDataState = clickKoreanDataState
        )
    } else if(clickKoreanData != null && clickKoreanData.state == "오답" ) {
        KoreanDialog(
            koreanData = clickKoreanData,
            onClose = onFailDialogCloseClick,
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
            fontSize = 32.sp, // Large font size
            fontWeight = FontWeight.Bold, // Bold text
            color = Color.Black // Text color
        )

        Button (
            onClick = onFilterClick
        ){
            Text(
            text = "필터",
            fontSize = 32.sp, // Large font size
            fontWeight = FontWeight.Bold, // Bold text
            color = Color.Black // Text color
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // 카드 사이 간격 추가
        ) {
            itemsIndexed(koreanDataList) { index, koreanData ->

                if(koreanData.state != "대기"){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(12.dp), // 둥근 테두리
                        elevation = CardDefaults.elevatedCardElevation(4.dp), // 그림자 효과
                        onClick = { onKoreanClick(koreanData) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if(koreanData.state == "완료"){
                                Image(
                                    painter = painterResource(id = R.drawable.star_gray),
                                    contentDescription = "Sample Vector Image",
                                    modifier = Modifier
                                        .size(20.dp),
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.star_yellow),
                                    contentDescription = "Sample Vector Image",
                                    modifier = Modifier
                                        .size(20.dp),
                                )
                            }
                            Text(koreanData.idiom)
                            Text(koreanData.korean)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 8.dp)
                            .background(color = Color.Cyan),
                        shape = RoundedCornerShape(12.dp), // 둥근 테두리
                        elevation = CardDefaults.elevatedCardElevation(4.dp), // 그림자 효과
                        onClick = { onKoreanClick(koreanData) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(koreanData.idiom)
                            Text(koreanData.korean)
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KoreanScreenPreview() {
    MypatTheme {
        KoreanScreen(
            koreanDataList = listOf(KoreanIdiom()),
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