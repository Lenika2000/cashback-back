package ru.itmo.bllab1.repo

import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime
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
        var startDate: LocalDateTime = LocalDateTime.now(),
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
        var cashbackSum: Double = 0.0
)

interface CashbackRepository : CrudRepository<Cashback, Long> {
    fun findCashbackByClient(client: Client): List<Cashback>
    fun findCashbackByShop(shop: Shop): List<Cashback>
    fun findByStartDateLessThan(date: LocalDateTime): List<Cashback>
}
