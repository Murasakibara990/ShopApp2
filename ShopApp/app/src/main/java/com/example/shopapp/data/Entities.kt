
package com.example.shopapp.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

object Role {
    const val USER = "USER"
    const val ADMIN = "ADMIN"
}

object Section {
    const val MEN = "ERKAKLAR"
    const val WOMEN = "AYOLLAR"
    const val SHOES = "POYABZAL"
    const val ACCESSORIES = "AKSESSUAR"
}

object OrderStatus {
    const val NEW = "NEW"
    const val ON_THE_WAY = "ON_THE_WAY"
    const val DELIVERED = "DELIVERED"
    const val CANCELLED = "CANCELLED"

    val all = listOf(NEW, ON_THE_WAY, DELIVERED, CANCELLED)

    fun label(code: String): String = when (code) {
        NEW -> "Yangi"
        ON_THE_WAY -> "Yo'lda"
        DELIVERED -> "Yetkazildi"
        CANCELLED -> "Bekor qilindi"
        else -> code
    }
}

object DeliveryMethod {
    const val COURIER = "COURIER"
    const val PICKUP = "PICKUP"
}

object PaymentMethod {
    const val CASH = "CASH"
    const val CARD = "CARD"
}

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val salt: String,
    val role: String = Role.USER,
    val addresses: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val price: Double,
    val brand: String,
    val section: String,
    val category: String,
    val images: String,
    val sizes: String,
    val colors: String,
    val isNew: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun Product.imageList(): List<String> =
    images.split("|").filter { it.isNotBlank() }

fun Product.sizeList(): List<String> =
    sizes.split("|").filter { it.isNotBlank() }

fun Product.colorList(): List<String> =
    colors.split("|").filter { it.isNotBlank() }

@Entity(tableName = "looks")
data class Look(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val coverImage: String
)

@Entity(
    tableName = "look_products",
    primaryKeys = ["lookId", "productId"],
    foreignKeys = [
        ForeignKey(Look::class, ["id"], ["lookId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Product::class, ["id"], ["productId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("productId")]
)
data class LookProductCrossRef(
    val lookId: Long,
    val productId: Long
)

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(Product::class, ["id"], ["productId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["userId", "productId", "size", "color"], unique = true),
        Index("productId")
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val size: String,
    val color: String,
    val quantity: Int = 1
)

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "productId"],
    foreignKeys = [
        ForeignKey(Product::class, ["id"], ["productId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("productId")]
)
data class Favorite(
    val userId: Long,
    val productId: Long
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val customerName: String,
    val phone: String,
    val address: String,
    val deliveryMethod: String,
    val paymentMethod: String,
    val total: Double,
    val status: String = OrderStatus.NEW,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(Order::class, ["id"], ["orderId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("orderId")]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val title: String,
    val image: String,
    val size: String,
    val color: String,
    val price: Double,
    val quantity: Int
)

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(Product::class, ["id"], ["productId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["productId", "userId"], unique = true)]
)
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val userId: Long,
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class CartLine(
    @Embedded val item: CartItem,
    @Relation(parentColumn = "productId", entityColumn = "id")
    val product: Product
)

data class LookWithProducts(
    @Embedded val look: Look,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = LookProductCrossRef::class,
            parentColumn = "lookId",
            entityColumn = "productId"
        )
    )
    val products: List<Product>
)

data class OrderWithItems(
    @Embedded val order: Order,
    @Relation(parentColumn = "id", entityColumn = "orderId")
    val items: List<OrderItem>
)

data class RatingSummary(
    val productId: Long,
    val avgRating: Double,
    val ratingCount: Int
)
