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
        "area/forest.jpg" to R.raw.bgm_positive,
        "area/beach.jpg" to R.raw.bgm_fun,
        // 필요하면 추가
        // "카페" to R.raw.bgm_cafe,
        // "해변" to R.raw.bgm_beach,
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
