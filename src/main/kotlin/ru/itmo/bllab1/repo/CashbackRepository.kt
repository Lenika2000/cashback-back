package ru.itmo.bllab1.repo

import Cashback
import Client
import Shop
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface CashbackRepository : CrudRepository<Cashback, Long> {
    fun findCashbackByClient(client: Client): List<Cashback>
    fun findCashbackByShop(shop: Shop): List<Cashback>
    fun findByStartDateLessThan(date: LocalDateTime): List<Cashback>
}
