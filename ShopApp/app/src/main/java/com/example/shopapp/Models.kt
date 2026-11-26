package com.example.shopapp

import org.json.JSONArray
import org.json.JSONObject

// 1. Товар
data class Product(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val emoji: String
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("title", title)
        obj.put("description", description)
        obj.put("price", price)
        obj.put("emoji", emoji)
        return obj
    }
}

// 2. Позиция в корзине
data class CartItem(
    val product: Product,
    var quantity: Int
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("product", product.toJson())
        obj.put("quantity", quantity)
        return obj
    }
}

// 3. Заказ
data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val date: String
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("total", total)
        obj.put("date", date)

        val itemsArray = JSONArray()
        for (item in items) {
            itemsArray.put(item.toJson())
        }
        obj.put("items", itemsArray)
        return obj
    }
}
