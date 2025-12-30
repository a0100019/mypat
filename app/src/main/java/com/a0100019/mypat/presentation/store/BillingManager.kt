package com.a0100019.mypat.presentation.store

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    private val billingClient: BillingClient
) {

    private var onBillingEvent: ((BillingEvent) -> Unit)? = null

    fun setBillingEventListener(listener: (BillingEvent) -> Unit) {
        onBillingEvent = listener
    }

    fun startPurchase(activity: Activity, productId: String) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { _, productDetailsList ->
            val productDetails = productDetailsList.firstOrNull()
                ?: return@queryProductDetailsAsync

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                )
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }

    fun handlePurchase(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.consumeAsync(consumeParams) { _, _ ->
                    onBillingEvent?.invoke(BillingEvent.PurchaseSuccess)
                }
            }
        }
    }
}


sealed interface BillingEvent {
    data object PurchaseSuccess : BillingEvent
    data class PurchaseFailed(val reason: String) : BillingEvent
}
