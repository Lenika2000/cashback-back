package ru.itmo.bllab1.repo

import org.springframework.data.repository.CrudRepository
import ru.itmo.bllab1.model.Cashback
import ru.itmo.bllab1.model.Client
import ru.itmo.bllab1.model.Shop
import java.time.LocalDateTime

interface CashbackRepository : CrudRepository<Cashback, Long> {
    fun findCashbackByClient(client: Client): List<Cashback>
    fun findCashbackByShop(shop: Shop): List<Cashback>
    fun findByStartDateLessThan(date: LocalDateTime): List<Cashback>
}
