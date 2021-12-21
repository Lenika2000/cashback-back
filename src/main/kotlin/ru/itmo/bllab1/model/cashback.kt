package ru.itmo.bllab1.model

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

enum class CashbackStatus {
    NEW,
    RECEIVED_INF,
    APPROVED,
    REJECTED,
}

@Entity
class Cashback(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,
        var creationDate: LocalDateTime = LocalDateTime.now(),
        @ManyToOne
        var client: Client = Client(),
        @ManyToOne
        var shop: Shop = Shop(),
        @Column(name = "is_paid")
        var isPaid: Boolean = false,
        @Column(name = "is_order_completed")
        var isOrderCompleted: Boolean = false,
        @Column(name = "confirm_payment")
        var confirmPayment: Boolean = false,
        var status: CashbackStatus = CashbackStatus.NEW,
        @Column(name = "cashback_sum")
        var cashbackSum: Double = 0.0,
        @Column(name = "product_name")
        var productName: String = "",
        @Column(name = "product_price")
        var productPrice: Double = 0.0,
        @Column(name = "shop_payment")
        var shopPayment: Double = 0.0,
)


data class CashbackData (
        val id: Long,
        val creationDate: LocalDateTime,
        val clientFirstName: String,
        val clientLastName: String,
        val shopName: String,
        var productName: String,
        var productPrice: Double,
        var isPaid: Boolean,
        var isOrderCompleted: Boolean,
        var confirmPayment: Boolean,
        var status: CashbackStatus,
        var cashbackSum: Double,
        var shopPayment: Double
)

data class CashbackDataForShop (
        val id: Long,
        val creationDate: LocalDateTime,
        val clientFirstName: String,
        val clientLastName: String,
        var productName: String,
        var productPrice: Double,
        var isPaid: Boolean,
        var isOrderCompleted: Boolean,
        var confirmPayment: Boolean,
        var status: CashbackStatus,
        var cashbackSum: Double,
        var shopPayment: Double
)

data class CashbackDataForClient (
        val id: Long,
        val creationDate: LocalDateTime,
        val shopName: String,
        var productName: String,
        var productPrice: Double,
        var status: CashbackStatus,
        var cashbackSum: Double,
)

const val KAFKA_CASHBACK_TOPIC = "test";

data class CashbackFromGenerator (
        val shopLogin: String,
        val clientLogin: String,
        var productName: String,
        var productPrice: Double,
        var creationDate: Calendar
)
