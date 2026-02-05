package com.a0100019.mypat.presentation.main.management

import android.app.Activity
import android.content.Context
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.LoadAdError
//import com.google.android.gms.ads.rewarded.RewardedAd
//import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
//    private var rewardedAd: RewardedAd? = null
//
//    // 테스트 광고 ID
//    private val adUnitId =
//        "ca-app-pub-5556462457265216/1571186590"
//
//    fun load() {
//        val adRequest = AdRequest.Builder().build()
//
//        RewardedAd.load(
//            context,
//            adUnitId,
//            adRequest,
//            object : RewardedAdLoadCallback() {
//
//                override fun onAdLoaded(ad: RewardedAd) {
//                    rewardedAd = ad
//                }
//
//                override fun onAdFailedToLoad(error: LoadAdError) {
//                    rewardedAd = null
//                }
//            }
//        )
//    }
//
//    fun show(
//        activity: Activity,
//        onReward: () -> Unit,
//        onNotReady: () -> Unit
//    ) {
//        if (rewardedAd == null) {
//            load()
//            onNotReady()
//            return
//        }
//
//        rewardedAd?.show(activity) {
//            onReward()
//            rewardedAd = null
//            load()
//        }
//    }

}
