package ru.itmo.bllab1.controller

import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.repo.Shop
import ru.itmo.bllab1.repo.ShopRepository
import javax.persistence.EntityNotFoundException

data class RegisterShopRequest(
        val name: String,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api")
@RestController
class ShopController(
        private val shopRepository: ShopRepository,
) {

    @GetMapping("/shop/{id}")
    fun getShopData(@PathVariable id: Long): Shop = shopRepository.findById(id).orElseThrow {
        EntityNotFoundException("Рекламодатель с id $id не найден!")
    }

    @GetMapping("/shops")
    fun getShopsData(): Iterable<Shop> = shopRepository.findAll()

    @PostMapping("/shop/register")
    fun registerShop(@RequestBody payload: RegisterShopRequest): MessageIdResponse {
        val shop = Shop(0, payload.name)
        shopRepository.save(shop)
        return MessageIdResponse("Пользователь успешно зарегистрирован", shop.id)
    }
}
