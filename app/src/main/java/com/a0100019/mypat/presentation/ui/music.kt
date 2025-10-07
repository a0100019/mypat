package com.a0100019.mypat.presentation.ui

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.a0100019.mypat.R
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun MusicPlayer(
    id: Int = 0,
    music: String = "",
    isLooping: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    if (id != 0) {
        LaunchedEffect(id, isLooping) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, id).apply {
                this.isLooping = isLooping
                start()
            }
        }
    } else {

        val resId = when(music) {
            //펫
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

            //아이템

            //맵
            //MainActivity 랑 맞추기, 맵은 area/forest.png -> 이런식으로
            "area/normal.webp" -> R.raw.bgm_positive2
            "area/normal_sakura.webp" -> R.raw.bgm_calm3
            "area/normal_snow.webp" -> R.raw.bgm_christmas
            "area/fall.webp" -> R.raw.bgm_positive5
            "area/city_sun2.webp" -> R.raw.bgm_dark
            "area/war2.webp" -> R.raw.bgm_awesome4
            "area/christmas3.webp" -> R.raw.bgm_christmas3
            "area/cemetery.webp" -> R.raw.bgm_nervous
            "area/island_sky.webp" -> R.raw.bgm_positive4
            "area/christmas2.webp" -> R.raw.bgm_christmas4

            "area/universe.webp" -> R.raw.bgm_fun4
            "area/japan.webp" -> R.raw.bgm_japan
            "area/city_dark.webp" -> R.raw.bgm_dark2
            "area/china2.webp" -> R.raw.bgm_china
            "area/night_sky.webp" -> R.raw.bgm_nervous3
            "area/ice_hot.webp" -> R.raw.bgm_awesome5
            "area/house.webp" -> R.raw.bgm_fun2
            "area/neon.webp" -> R.raw.bgm_fun6
            "area/forest_beautiful.webp" -> R.raw.bgm_calm
            "area/winter3.webp" -> R.raw.bgm_christmas2

            "area/rain_train.webp" -> R.raw.bgm_dark3
            "area/hell.webp" -> R.raw.bgm_awesome
            "area/house_normal1.webp" -> R.raw.bgm_positive
            "area/kingdom.webp" -> R.raw.bgm_positive3
            "area/hospital_dark.webp" -> R.raw.bgm_scary
            "area/ice_heaven.webp" -> R.raw.bgm_awesome3
            "area/house_dark.webp" -> R.raw.bgm_nervous2
            "area/old_ice.webp" -> R.raw.bgm_awesome2
            "area/house_pink1.webp" -> R.raw.bgm_positive6
            "area/wall.webp" -> R.raw.bgm_fun5

            "area/sea_sun.webp" -> R.raw.bgm_calm4
            "area/earthquake.webp" -> R.raw.bgm_fun3
            "area/jelly.webp" -> R.raw.bgm_fun
            "area/forest_magic.webp" -> R.raw.bgm_calm2

            else -> null
        }

        LaunchedEffect(resId, isLooping) {
            mediaPlayer?.release()
            if (resId != null) {
                mediaPlayer = MediaPlayer.create(context, resId).apply {
                    this.isLooping = isLooping
                    start()
                }
            } else {
                mediaPlayer = null
                Log.d("music", "해당하는 사운드 없음 → 소리 재생 안 함")
            }
        }
    }

    // 🔹 앱이 백그라운드(ON_STOP) 되면 일시정지, 포그라운드(ON_START)면 재개(원하면 주석 처리)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> mediaPlayer?.pause()   // 홈 키로 나가면 멈춤
                Lifecycle.Event.ON_DESTROY -> {
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                // 필요 시 복귀 시 자동 재생 원치 않으면 아래 ON_START는 빼세요
                Lifecycle.Event.ON_START -> if (isLooping) mediaPlayer?.start()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}


object SfxPlayer {
    fun play(context: Context, @RawRes resId: Int) {
        val mp = MediaPlayer.create(context, resId)
        mp.setOnCompletionListener { it.release() }
        mp.start()
    }
}