package com.a0100019.mypat.presentation.game.thirdGame

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.game.secondGame.SecondGameSideEffect
import com.a0100019.mypat.presentation.game.secondGame.SecondGameState
import com.a0100019.mypat.presentation.game.secondGame.SecondGameViewModel
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ThirdGameScreen(
    thirdGameViewModel: ThirdGameViewModel = hiltViewModel()

) {

    val thirdGameState : ThirdGameState = thirdGameViewModel.collectAsState().value

    val context = LocalContext.current

    thirdGameViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ThirdGameSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    ThirdGameScreen(
        value = "스크린 나누기"
    )
}



@Composable
fun ThirdGameScreen(
    value : String
) {
    // Fullscreen container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional: Set background color
        contentAlignment = Alignment.Center // Center content
    ) {
        // Text in the center
        Text(
            text = "로딩 중",
            fontSize = 32.sp, // Large font size
            fontWeight = FontWeight.Bold, // Bold text
            color = Color.Black // Text color
        )
        KoreanIdiomImage("koreanIdiomImage/jukmagow1.jpg")
    }
}

@Preview(showBackground = true)
@Composable
fun ThirdGameScreenPreview() {
    MypatTheme {
        ThirdGameScreen(
            value = ""
        )
    }
}