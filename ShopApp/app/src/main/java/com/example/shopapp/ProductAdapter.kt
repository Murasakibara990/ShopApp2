package com.example.shopapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class ProductAdapter(
    private val context: Context,
    private val products: List<Product>,
    private val onAddClick: (Product) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = products.size
    override fun getItem(position: Int): Any = products[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        val product = products[position]

        val emojiText = view.findViewById<TextView>(R.id.emojiText)
        val titleText = view.findViewById<TextView>(R.id.titleText)
        val priceText = view.findViewById<TextView>(R.id.priceText)
        val addButton = view.findViewById<Button>(R.id.addButton)

        emojiText.text = product.emoji
        titleText.text = product.title
        priceText.text = "${product.price.toInt()} ₽"

        addButton.setOnClickListener { onAddClick(product) }

        return view
    }
}

