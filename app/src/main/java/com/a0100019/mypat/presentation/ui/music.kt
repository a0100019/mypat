package com.a0100019.mypat.presentation.ui

import android.media.MediaPlayer
import android.util.Log
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
    id: Int = 0,         // 리소스 직접 입력
    music: String = "", // 리소스 이름 입력
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

        val resId = when(music) {
            "고양이" -> R.raw.cry_cat
            "강아지" -> R.raw.cry_dog2
            "춤추는 말랑이" -> R.raw.slime9
            "도형 친구들" -> R.raw.slime8
            "산타 먼지" -> R.raw.wind4
            "커플 곰" -> R.raw.slime2
            "게이머 고양이" -> R.raw.slime6
            "장난꾸러기 펭귄" -> R.raw.short3
            "축하 고양이" -> R.raw.cry_cat2
            "헤헤멍" -> R.raw.cry_dog
            "힙합 라마" -> R.raw.cry_goat
            "미친 팽귄" -> R.raw.walk3
            "드래곤" -> R.raw.cry_dragon
            "할로윈 고양이" -> R.raw.cry_cat4
            "화난 젤리" -> R.raw.slime7
            "게으른 사슴" -> R.raw.short6
            "점프 슬라임" -> R.raw.slime4
            "노트북 쿼카" -> R.raw.keyboard
            "불 슬라임" -> R.raw.slime
            "유령" -> R.raw.short10
            "코알라" -> R.raw.short8
            "통통 슬라임" -> R.raw.slime5
            "박쥐" -> R.raw.short5
            "신난닭" -> R.raw.cry_chicken
            "쿠키와 우유" -> R.raw.positive
            "아보와 카도" -> R.raw.positive7
            "아기 공룡" -> R.raw.short4
            "팝콘 판다" -> R.raw.slime3
            "힙합 샌드위치" -> R.raw.positive6
            "명상 나무늘보" -> R.raw.snoring
            "둘기" -> R.raw.cute
            "북극곰" -> R.raw.positive2
            "음악가 호랑이" -> R.raw.cry_monster
            "치즈냥이" -> R.raw.cry_cat3
            "어둠 유령" -> R.raw.scary5
            "마법 소녀" -> R.raw.magic

            else -> null
        }

        LaunchedEffect(resId, isLooping) {
            mediaPlayer?.release()

            if (resId != null) {
                mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer?.isLooping = isLooping
                mediaPlayer?.start()
            } else {
                mediaPlayer = null
                Log.d("music", "해당하는 사운드 없음 → 소리 재생 안 함")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

}
