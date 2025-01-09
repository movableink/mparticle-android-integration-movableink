package com.movableink.mparticle
import android.content.Context
import android.content.Intent
import com.movableink.inked.MIClient
import com.movableink.inked.analytics.order.OrderCompletedProperties
import com.movableink.inked.analytics.order.OrderProduct
import com.movableink.inked.analytics.product.Category
import com.movableink.inked.analytics.product.ProductSearched
import com.movableink.mparticle.MovableEventType.CATEGORY_VIEWED
import com.movableink.mparticle.MovableEventType.ORDER_COMPLETED
import com.movableink.mparticle.MovableEventType.PRODUCT_ADDED
import com.movableink.mparticle.MovableEventType.PRODUCT_REMOVED
import com.movableink.mparticle.MovableEventType.PRODUCT_SEARCHED
import com.movableink.mparticle.MovableEventType.PRODUCT_VIEWED
import com.mparticle.AttributionError
import com.mparticle.AttributionListener
import com.mparticle.AttributionResult
import com.mparticle.MPEvent
import com.mparticle.commerce.CommerceEvent
import com.mparticle.kits.KitIntegration
import com.mparticle.kits.KitIntegration.ApplicationStateListener
import com.mparticle.kits.ReportingMessage
import org.json.JSONObject
import java.math.BigDecimal

const val TAG = "KitIntegration"
private const val MOVABLE_APP_KEY = "movableAppKey"

class MovableKit :
    KitIntegration(),
    AttributionListener,
    KitIntegration.CommerceListener,
    ApplicationStateListener {
    override fun getName(): String = NAME

    override fun onKitCreate(
        settings: Map<String?, String?>?,
        context: Context?,
    ): List<ReportingMessage?>? {
        getSettings()[MOVABLE_APP_KEY]
            ?.let { MIClient.start() }

        // Initialize deep link handling
        return listOf()
    }

    override fun setOptOut(optedOut: Boolean): List<ReportingMessage?>? =
        listOf(
            ReportingMessage(
                this,
                ReportingMessage.MessageType.OPT_OUT,
                System.currentTimeMillis(),
                null,
            ),
        )

    override fun logEvent(event: MPEvent): List<ReportingMessage> {
        when (event.eventName) {
            PRODUCT_SEARCHED -> {
                val properties =
                    ProductSearched(
                        query = event.customAttributeStrings?.get("query").toString(),
                        url = event.customAttributeStrings?.get("url"),
                    )
                MIClient.productSearched(properties)
            }
            CATEGORY_VIEWED -> {
                val category =
                    Category(
                        id = event.category.toString(),
                        url = event.customAttributeStrings?.get("url"),
                        title = event.category,
                    )
                MIClient.categoryViewed(category)
            }

            else -> {}
        }

        return listOf(
            ReportingMessage(
                this,
                ReportingMessage.MessageType.EVENT,
                System.currentTimeMillis(),
                event.customAttributeStrings,
            ),
        )
    }

    override fun logEvent(commerceEvent: CommerceEvent): List<ReportingMessage> {
        when (commerceEvent.eventName) {
            PRODUCT_ADDED -> {
                commerceEvent.products?.forEach { product -> MIClient.productAdded(createProductProperties(product)) }
            }
            PRODUCT_REMOVED -> {
                commerceEvent.products?.forEach { product ->
                    MIClient.productRemoved(createProductProperties(product))
                }
            }

            PRODUCT_VIEWED -> {
                commerceEvent.products?.forEach { product -> MIClient.productViewed(createProductProperties(product)) }
            }
            ORDER_COMPLETED -> {
                commerceEvent.products?.forEach { product ->
                    val properties =
                        OrderCompletedProperties(
                            id = product.sku,
                            revenue = product.totalAmount.toString(),
                            orderProducts =
                                listOf(
                                    OrderProduct(
                                        id = product.sku,
                                        title = product.name,
                                        price = product.unitPrice.toString(),
                                        quantity = product.quantity.toInt(),
                                    ),
                                ),
                        )
                    MIClient.orderCompleted(properties)
                }
            }
        }
        return listOf(ReportingMessage.fromEvent(this, commerceEvent))
    }

    override fun onResult(result: AttributionResult) {
           /* val clickthroughUrl = result.link
            if (!clickthroughUrl.isNullOrEmpty()) {
              result
            }*/
    }

    override fun onError(p0: AttributionError) {}

    override fun logLtvIncrease(
        bigDecimal: BigDecimal,
        bigDecimal1: BigDecimal,
        s: String,
        map: Map<String, String>,
    ): List<ReportingMessage> = emptyList()

    override fun onApplicationForeground() {
        val currentActivity = kitManager.currentActivity?.get()
        currentActivity?.intent?.data?.let { uri ->
            MIClient.resolveUrlAsync(uri.toString()) { resolvedLink ->
                resolvedLink?.let {
                    val attributionResult =
                        AttributionResult().apply {
                            parameters =
                                JSONObject().apply {
                                    put("clickthrough_url", it)
                                }
                            serviceProviderId = configuration.kitId
                        }
                    kitManager.onResult(attributionResult)
                }
            }
        }
    }

    override fun setInstallReferrer(intent: Intent?) {
        // Called when a deep link is received via an Intent
        super.setInstallReferrer(intent)
       /* intent?.data?.let
       { uri ->

        }*/
    }

    override fun onApplicationBackground() {}

    private fun onDeepLinkError(error: Exception) {
        val attributionError =
            AttributionError().apply {
                message = error.message
                serviceProviderId = configuration.kitId
            }
        kitManager.onError(attributionError)
    }

    companion object {
        const val NAME = "Movable Ink Kit"
    }
}
