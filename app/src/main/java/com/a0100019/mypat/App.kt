package com.a0100019.mypat

import android.app.Application
//import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

//        MobileAds.initialize(this)
    }
}
