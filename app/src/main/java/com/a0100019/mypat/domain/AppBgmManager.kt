package com.a0100019.mypat.domain

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.a0100019.mypat.R

object AppBgmManager {
    private var player: MediaPlayer? = null
    private var prepared = false
    private var currentResId: Int? = null
    private var currentName: String? = null

    // 이름 -> 리소스 매핑만 여기서 관리하면 됨
    private val nameToRes: Map<String, Int> = mapOf(

        "area/normal.png" to R.raw.bgm_positive2,
        "area/normal_sakura.png" to R.raw.bgm_calm3,
        "area/normal_snow.png" to R.raw.bgm_christmas,
        "area/fall.png" to R.raw.bgm_positive5,
        "area/city_sun2.png" to R.raw.bgm_dark,
        "area/war2.png" to R.raw.bgm_awesome4,
        "area/christmas3.png" to R.raw.bgm_christmas3,
        "area/cemetery.png" to R.raw.bgm_nervous,
        "area/island_sky.png" to R.raw.bgm_positive4,
        "area/christmas2.png" to R.raw.bgm_christmas4,

        "area/universe.png" to R.raw.bgm_fun4,
        "area/japan.png" to R.raw.bgm_japan,
        "area/city_dark.png" to R.raw.bgm_dark2,
        "area/china2.png" to R.raw.bgm_china,
        "area/night_sky.png" to R.raw.bgm_nervous3,
        "area/ice_hot.png" to R.raw.bgm_awesome5,
        "area/house.png" to R.raw.bgm_fun2,
        "area/neon.png" to R.raw.bgm_fun6,
        "area/forest_beautiful.png" to R.raw.bgm_calm,
        "area/winter3.png" to R.raw.bgm_christmas2,

        "area/rain_train.png" to R.raw.bgm_dark3,
        "area/hell.png" to R.raw.bgm_awesome,
        "area/house_normal1.png" to R.raw.bgm_positive,
        "area/kingdom.png" to R.raw.bgm_positive3,
        "area/hospital_dark.png" to R.raw.bgm_scary,
        "area/ice_heaven.png" to R.raw.bgm_awesome3,
        "area/house_dark.png" to R.raw.bgm_nervous2,
        "area/old_ice.png" to R.raw.bgm_awesome2,
        "area/house_pink1.png" to R.raw.bgm_positive6,
        "area/wall.png" to R.raw.bgm_fun5,

        "area/sea_sun.png" to R.raw.bgm_calm4,
        "area/earthquake.png" to R.raw.bgm_fun3,
        "area/jelly.png" to R.raw.bgm_fun,
        "area/forest_magic.png" to R.raw.bgm_calm2,

    )

    /** 이름으로 초기화 */
    fun init(
        context: Context,
        name: String,
        loop: Boolean = true,
        volume: Float = 0.5f,
        defaultName: String = "area/forest.jpg"
    ) {
        if (prepared) return
        val resId = resolve(name) ?: resolve(defaultName) ?: return
        internalStart(context.applicationContext, resId, loop, volume)
        currentName = name
    }

    /** 이름으로 트랙 교체 (즉시 전환) */
    fun changeTrack(
        context: Context,
        name: String,
        keepPosition: Boolean = false,
        loop: Boolean = true
    ) {
        val resId = resolve(name) ?: return
        if (currentResId == resId && prepared) return

        val wasPlaying = player?.isPlaying == true
        val volume = getCurrentVolume()
        val pos = if (keepPosition && prepared) player?.currentPosition ?: 0 else 0

        release()
        player = MediaPlayer.create(context.applicationContext, resId).apply {
            isLooping = loop
            setVolume(volume, volume)
            if (pos > 0) seekTo(pos)
            if (wasPlaying) start()
        }
        prepared = true
        currentResId = resId
        currentName = name
    }

    /** 재생/일시정지/볼륨 */
    fun play() { if (prepared) player?.start() }
    fun pause() { player?.pause() }
    fun toggle(on: Boolean) { if (on) play() else pause() }
    fun setVolume(v: Float) {
        val vol = v.coerceIn(0f, 1f)
        player?.setVolume(vol, vol)
    }
    fun isPlaying(): Boolean = player?.isPlaying == true
    fun currentTrackResId(): Int? = currentResId
    fun currentTrackName(): String? = currentName

    /** 모두 해제 */
    fun release() {
        prepared = false
        player?.release()
        player = null
        currentResId = null
        // currentName은 남겨두고 싶으면 주석 처리
        // currentName = null
    }

    // 내부 공용
    private fun internalStart(context: Context, @RawRes resId: Int, loop: Boolean, volume: Float) {
        release()
        player = MediaPlayer.create(context, resId).apply {
            isLooping = loop
            setVolume(volume.coerceIn(0f,1f), volume.coerceIn(0f,1f))
            start()
        }
        prepared = true
        currentResId = resId
    }

    private fun resolve(name: String): Int? = nameToRes[name]

    private fun getCurrentVolume(): Float {
        // 필요하면 setVolume 호출 시 값을 저장해두고 여기서 반환하도록 바꿔도 됩니다.
        return 0.5f
    }
}
