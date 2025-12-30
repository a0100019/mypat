package com.a0100019.mypat.presentation.store

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
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

    @Provides
    @Singleton
    fun provideBillingClient(
        @ApplicationContext context: Context,
        billingManagerProvider: Provider<BillingManager>
    ): BillingClient {

        return BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases?.let {
                        billingManagerProvider.get().handlePurchase(it)
                    }
                }
            }
            .enablePendingPurchases()
            .build()
            .also {
                it.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(result: BillingResult) {}
                    override fun onBillingServiceDisconnected() {}
                })
            }
    }
}
