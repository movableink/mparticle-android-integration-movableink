package com.movableink.mparticle

import com.movableink.inked.analytics.product.Category
import com.movableink.inked.analytics.product.ProductProperties
import com.mparticle.commerce.Product

object MovableEventType {
    const val PRODUCT_ADDED = "add_to_cart"
    const val PRODUCT_REMOVED = "remove_from_cart"
    const val PRODUCT_VIEWED = "view_detail"
    const val ORDER_COMPLETED = "purchase"
    const val CATEGORY_VIEWED = "CategoryViewed"
    const val PRODUCT_SEARCHED = "ProductSearched"
}

fun createProductProperties(product: Product): ProductProperties =
    ProductProperties(
        id = product.sku,
        title = product.name,
        price = product.unitPrice.toString(),
        url = null,
        categories =
            listOf(
                Category(
                    id = product.category.toString(),
                    title = null,
                    url = null,
                ),
            ),
    )

fun List<Product>.toProductPropertiesList(): List<ProductProperties> = this.map { createProductProperties(it) }
