package ru.itmo.bllab1.repo

import org.springframework.data.repository.CrudRepository
import ru.itmo.bllab1.model.Shop
import java.util.*

interface ShopRepository : CrudRepository<Shop, Long> {
        fun findShopByName(name: String): Optional<Shop>
}
