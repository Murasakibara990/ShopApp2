package com.example.shopapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class LocalRepo(context: Context) {
    // SharedPreferences - это локальная память телефона (как маленькая БД)
    private val prefs = context.getSharedPreferences("shop_prefs", Context.MODE_PRIVATE)

    // Получить список товаров (в нашем случае они захардкожены, но в будущем тут может быть запрос к серверу)
    fun getProducts(): List<Product> {
        return listOf(
            Product("1", "Пицца Маргарита", "30 см, томаты, моцарелла", 590.0, "🍕"),
            Product("2", "Бургер Классик", "Говядина, сыр, булочка", 390.0, "🍔"),
            Product("3", "Капучино", "300 мл, двойной эспрессо", 220.0, "☕"),
            Product("4", "Футболка Oversize", "Хлопок 100%", 1290.0, "👕"),
            Product("5", "Кроссовки Runner", "Легкая подошва", 4990.0, "👟")
        )
    }

    // Сохранить корзину
    fun saveCart(cart: List<CartItem>) {
        val array = JSONArray()
        for (item in cart) array.put(item.toJson())
        prefs.edit().putString("cart", array.toString()).apply()
    }

    // Загрузить корзину
    fun loadCart(): MutableList<CartItem> {
        val cart = mutableListOf<CartItem>()
        val jsonStr = prefs.getString("cart", "[]") ?: "[]"
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val pObj = obj.getJSONObject("product")
                val p = Product(
                    pObj.getString("id"),
                    pObj.getString("title"),
                    pObj.getString("description"),
                    pObj.getDouble("price"),
                    pObj.getString("emoji")
                )
                cart.add(CartItem(p, obj.getInt("quantity")))
            }
        } catch (e: Exception) { e.printStackTrace() }
        return cart
    }

    // Сохранить заказ в историю
    fun saveOrder(order: Order) {
        val ordersStr = prefs.getString("orders", "[]") ?: "[]"
        try {
            val array = JSONArray(ordersStr)
            array.put(order.toJson())
            prefs.edit().putString("orders", array.toString()).apply()
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Очистить корзину после заказа
    fun clearCart() {
        prefs.edit().remove("cart").apply()
    }
}
