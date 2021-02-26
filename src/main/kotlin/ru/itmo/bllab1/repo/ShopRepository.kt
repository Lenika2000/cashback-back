package ru.itmo.bllab1.repo

import Shop
import org.springframework.data.repository.CrudRepository
import java.util.*
import javax.persistence.*

interface ShopRepository : CrudRepository<Shop, Long> {
        fun findShopByName(name: String): Optional<Shop>
}
