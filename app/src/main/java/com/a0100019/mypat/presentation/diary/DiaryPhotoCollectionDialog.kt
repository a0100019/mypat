package com.a0100019.mypat.presentation.diary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.a0100019.mypat.data.room.photo.Photo
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryPhotoCollectionDialog(
    onClose: () -> Unit,
    onPhotoClick: (String) -> Unit = {},
    photoDataList: List<Photo> = emptyList()
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        // 다이얼로그 바깥쪽 여백 및 정렬
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // [다이얼로그 메인 컨테이너]
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(32.dp), // 아주 둥글게 해서 귀여움 강조
                color = Color(0xFFFDFDFD), // 뽀얀 미색 배경
                border = BorderStroke(2.dp, Color(0xFFE1BEE7).copy(alpha = 0.5f)), // 연보라색 테두리
                shadowElevation = 15.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 상단 장식 (귀여운 포인트)
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(Color(0xFFE0E0E0), CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "추억 보관함",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 1. 데이터를 날짜별로 그룹화
                    val groupedPhotos = photoDataList.sortedByDescending { it.date }.groupBy { it.date }

                    if(photoDataList.isNotEmpty()){
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2), // 🔹 한 줄에 2개씩 출력
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 500.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp), // 간격을 조금 더 넓혀서 시원하게
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            groupedPhotos.forEach { (date, photos) ->

                                // [날짜 헤더] - 한 줄을 통째로 차지 (span을 columns와 동일하게 2로 설정)
                                item(span = { GridItemSpan(2) }) {
                                    Column {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = date,
                                            style = MaterialTheme.typography.titleSmall.copy( // 글씨 크기 살짝 키움
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF5D4037)
                                            ),
                                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.5.dp)
                                                .background(Color(0xFFE1BEE7).copy(alpha = 0.6f)) // 조금 더 선명한 구분선
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }

                                // [해당 날짜의 사진들]
                                items(photos) { photo ->
                                    Surface(
                                        modifier = Modifier
                                            .aspectRatio(1f) // 2개씩이라 사진이 큼직해짐
                                            .clip(RoundedCornerShape(16.dp)), // 사진이 커진 만큼 모서리도 더 둥글게
                                        border = BorderStroke(1.5.dp, Color(0xFFF3E5F5)),
                                        shadowElevation = 3.dp // 입체감 살짝 추가
                                    ) {
                                        AsyncImage(
                                            model = photo.localPath,
                                            contentDescription = "일기 사진",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable { onPhotoClick(photo.localPath) },
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    } else {

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "저장된 사진이 없습니다.\n\n사진은 서버에 안전하게 암호화되어 저장됩니다.\n\n로그인 된 본인만 확인할 수 있으니, 걱정 없이 오늘 하루를 남겨보세요!",
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 하단 닫기 버튼
                    Surface(
                        onClick = onClose,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFBBDEFB).copy(alpha = 0.3f), // 연하늘색 배경
                        modifier = Modifier
                            .height(44.dp)
                            .fillMaxWidth(0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "닫기",
                                style = TextStyle(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryPhotoCollectionDialogPreview() {
    MypatTheme {
        DiaryPhotoCollectionDialog(
            onClose = {},
        )
    }
}