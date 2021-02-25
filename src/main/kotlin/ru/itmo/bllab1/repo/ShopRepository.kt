package ru.itmo.bllab1.repo

import org.springframework.data.repository.CrudRepository
import java.util.*
import javax.persistence.*

@Entity
class Shop(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,
        var name: String = "",
)

interface ShopRepository : CrudRepository<Shop, Long> {
        fun findShopByName(name: String): Optional<Shop>
}
