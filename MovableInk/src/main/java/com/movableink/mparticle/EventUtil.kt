package com.movableink.mparticle

import com.movableink.inked.analytics.EventType
import com.movableink.inked.analytics.product.Category
import com.movableink.inked.analytics.product.ProductProperties
import com.mparticle.MPEvent
import com.mparticle.commerce.Product


object MovableEventType {
    const val CATEGORY_VIEWED = "CategoryViewed"
    const val PRODUCT_SEARCHED = "ProductSearched"
    const val PRODUCT_ADDED = "ProductAdded"
    const val PRODUCT_REMOVED = "productRemoved"
    const val PRODUCT_VIEWED = "productViewed"
    const val ORDER_COMPLETED = "orderCompleted"
}



fun createProductProperties(product: Product): ProductProperties {
    return ProductProperties(
        id = product.sku,
        title = product.name,
        price = product.unitPrice.toString(),
        url = null,
        categories = listOf(
            Category(
                id = product.category.toString(),
                title = null,
                url = null
            )
        )
    )
}

fun List<Product>.toProductPropertiesList(): List<ProductProperties> {
    return this.map { createProductProperties(it) }
}