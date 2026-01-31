package com.a0100019.mypat.presentation.main.management

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAdManager {
    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    fun loadAd(context: Context) {
        if (mInterstitialAd != null || isAdLoading) return // 이미 있거나 로딩 중이면 중복 요청 방지

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, "ca-app-pub-5556462457265216/1872804383", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    isAdLoading = false
                }
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    isAdLoading = false
                }
            })
    }

    fun showAd(activity: Activity, onAdClosed: () -> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // 중요: 리스너 해제 및 광고 객체 초기화
                    mInterstitialAd?.fullScreenContentCallback = null
                    mInterstitialAd = null
                    loadAd(activity) // 다음 광고 미리 준비
                    onAdClosed()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    mInterstitialAd = null
                    onAdClosed()
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            // 광고가 준비 안 됐으면 바로 다음으로
            onAdClosed()
            loadAd(activity) // 지금이라도 로드 시도
        }
    }
}