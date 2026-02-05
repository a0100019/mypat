package com.a0100019.mypat.presentation.activity.store

import android.content.Context
//import com.android.billingclient.api.BillingClient
//import com.android.billingclient.api.BillingClientStateListener
//import com.android.billingclient.api.BillingResult
//import com.android.billingclient.api.PendingPurchasesParams
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

//    @Provides
//    @Singleton
//    fun provideBillingClient(
//        @ApplicationContext context: Context,
//        billingManagerProvider: Provider<BillingManager>
//    ): BillingClient {
//
//        // 1. PendingPurchasesParams 객체 생성 (버전 7.0.0 이상 필수 사항)
//        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
//            .enableOneTimeProducts() // 일회성 상품(코인 등) 결제 허용
//            // 만약 구독 상품도 있다면 아래 주석을 해제하세요.
//            // .enablePrepaidPlans()
//            .build()
//
//        return BillingClient.newBuilder(context)
//            .setListener { billingResult, purchases ->
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    purchases?.let {
//                        billingManagerProvider.get().handlePurchase(it)
//                    }
//                }
//            }
//            // 2. 파라미터가 없는 이전 함수 대신, 생성한 객체를 전달합니다.
//            .enablePendingPurchases(pendingPurchasesParams)
//            .build()
//            .also {
//                it.startConnection(object : BillingClientStateListener {
//                    override fun onBillingSetupFinished(result: BillingResult) {
//                        // 초기화 성공 여부를 로그로 확인하면 좋습니다.
//                    }
//                    override fun onBillingServiceDisconnected() {
//                        // 연결 끊김 시 재연결 로직이 BillingManager에 있으면 좋습니다.
//                    }
//                })
//            }
//    }
}