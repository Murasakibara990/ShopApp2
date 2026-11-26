package com.example.shopapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var repo: LocalRepo
    private lateinit var productsListView: ListView
    private lateinit var cartButton: Button

    private var cart = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repo = LocalRepo(this)
        cart = repo.loadCart() // Загружаем корзину из памяти при запуске

        productsListView = findViewById(R.id.productsListView)
        cartButton = findViewById(R.id.cartButton)

        setupCatalog()
        updateCartButton()

        // Обработка клика по кнопке "Корзина"
        cartButton.setOnClickListener {
            showCartDialog()
        }
    }

    private fun setupCatalog() {
        val products = repo.getProducts()
        // Передаем колбэк: что делать, когда нажали "+"
        val adapter = ProductAdapter(this, products) { product ->
            addToCart(product)
        }
        productsListView.adapter = adapter
    }

    private fun addToCart(product: Product) {
        val existingItem = cart.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cart.add(CartItem(product, 1))
        }
        repo.saveCart(cart) // Сохраняем в локальную память
        updateCartButton()
        Toast.makeText(this, "${product.title} добавлен", Toast.LENGTH_SHORT).show()
    }

    private fun updateCartButton() {
        val count = cart.sumOf { it.quantity }
        val total = cart.sumOf { it.product.price * it.quantity }
        cartButton.text = "Корзина ($count) | ${total.toInt()} ₽"
    }

    // --- Логика отображения корзины ---
    private fun showCartDialog() {
        if (cart.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        // Формируем текст корзины
        val sb = StringBuilder()
        for (item in cart) {
            sb.append("${item.product.emoji} ${item.product.title} x${item.quantity} = ${(item.product.price * item.quantity).toInt()} ₽\n")
        }

        AlertDialog.Builder(this)
            .setTitle("Ваша корзина")
            .setMessage(sb.toString())
            .setPositiveButton("Оформить заказ") { _, _ ->
                showCheckoutDialog()
            }
            .setNegativeButton("Закрыть", null)
            .show()
    }

    // --- Логика оформления заказа ---
    private fun showCheckoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Оформление заказа")

        // Создаем простую форму программно (чтобы не плодить XML файлы)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 40)

        val nameInput = EditText(this).apply { hint = "Ваше имя" }
        val phoneInput = EditText(this).apply { hint = "Телефон" }

        layout.addView(nameInput)
        layout.addView(phoneInput)
        builder.setView(layout)

        builder.setPositiveButton("Заказать") { _, _ ->
            if (nameInput.text.isBlank() || phoneInput.text.isBlank()) {
                Toast.makeText(this, "Заполните имя и телефон", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Создаем заказ
            val total = cart.sumOf { it.product.price * it.quantity }
            val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
            val order = Order(
                id = System.currentTimeMillis().toString(),
                items = cart.toList(),
                total = total,
                date = date
            )

            repo.saveOrder(order)
            repo.clearCart()
            cart.clear()
            updateCartButton()

            Toast.makeText(this, "Заказ успешно оформлен!", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("Отмена", null)
        builder.show()
    }
}