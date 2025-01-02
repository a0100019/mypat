package com.a0100019.mypat.presentation.main.world

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.image.DisplayKoreanIdiomImage
import com.a0100019.mypat.presentation.image.DisplayMapImage
import com.a0100019.mypat.presentation.image.Pat
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.presentation.main.MainSideEffect
import com.a0100019.mypat.presentation.main.MainState
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

//@Composable
//fun WorldScreen(
//    viewModel: WorldViewModel? = null
//
//) {
//
//    val state : WorldState = viewModel?.collectAsState()!!.value
//
//    val context = LocalContext.current
//
//    viewModel.collectSideEffect { sideEffect ->
//        when (sideEffect) {
//            is WorldSideEffect.Toast ->
//                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
//
//        }
//    }
//
//
//    WorldScreen(
//        value = "스크린 나누기"
//    )
//}



@Composable
fun WorldScreen(
    mapUrl : String,
    firstPatData : Pat,
    firstPatWorldData : World
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth() // 가로 크기는 최대
            .aspectRatio(1 / 1.25f) // 세로가 가로의 1.25배
            .padding(10.dp), // padding 추가
        color = Color.Gray
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Optional: Set background color
            contentAlignment = Alignment.Center // Center content
        ) {
            DisplayMapImage(mapUrl)
            // Text in the center
//            Text(
//                text = "로딩 중",
//                fontSize = 32.sp, // Large font size
//                fontWeight = FontWeight.Bold, // Bold text
//                color = Color.Black // Text color
//            )
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val density = LocalDensity.current

                // Surface 크기 가져오기 (px → dp 변환)
                val surfaceWidth = constraints.maxWidth
                val surfaceHeight = constraints.maxHeight

                val surfaceWidthDp = with(density) { surfaceWidth.toDp() }
                val surfaceHeightDp = with(density) { surfaceHeight.toDp() }


                Pat(
                    patUrl = firstPatData.url,
                    surfaceWidthDp = surfaceWidthDp,
                    surfaceHeightDp = surfaceHeightDp,
                    xFloat = firstPatWorldData.x,
                    yFloat = firstPatWorldData.y,
                    sizeFloat = firstPatData.sizeFloat
                )

            }

        }

    }

    // Fullscreen container

}

@Preview(showBackground = true)
@Composable
fun SelectScreenPreview() {
    MypatTheme {
        WorldScreen(
            mapUrl = "map/beach.jpg",
            firstPatData = Pat(url = ""),
            firstPatWorldData = World(id = "")
        )
    }
}