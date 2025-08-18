package com.a0100019.mypat.presentation.ui

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.a0100019.mypat.R

@Composable
fun MusicPlayer(
    id: Int = 0,         // 재생할 음악 리소스
    music: String = "",
    isLooping: Boolean = false // 반복 여부
) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    if(id != 0) {

        LaunchedEffect(id, isLooping) { // 값이 바뀌면 다시 실행
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, id)
            mediaPlayer?.isLooping = isLooping
            mediaPlayer?.start()
        }

    } else {

//        val resId = when(music) {
//            "bgm1" -> R.raw.bmg1
//            else -> R.raw.bmg1
//        }
//
//        LaunchedEffect(resId, isLooping) { // 값이 바뀌면 다시 실행
//            mediaPlayer?.release()
//            mediaPlayer = MediaPlayer.create(context, resId)
//            mediaPlayer?.isLooping = isLooping
//            mediaPlayer?.start()
//        }

    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

}
