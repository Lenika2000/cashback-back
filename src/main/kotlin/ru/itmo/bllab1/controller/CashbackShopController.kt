package ru.itmo.bllab1.controller

import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.repo.*
import ru.itmo.bllab1.service.CashbackService
import ru.itmo.bllab1.service.MessageService
import javax.persistence.EntityNotFoundException

data class CashbackChangeRequestPayload(
        val id: Long,
        val isPaid: Boolean?,
        val isOrderCompleted: Boolean?,
        val payment: Double?,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api/shop/cashback/")
@RestController
class CashbackShopController(
        private val cashbackService: CashbackService,
        private val cashbackRepository: CashbackRepository,
        private val shopRepository: ShopRepository,
) {

    @PostMapping("update")
    fun updateCashback(@RequestBody payload: CashbackChangeRequestPayload): CashbackResponse {
        val cashbackId = cashbackService.updateCashback(payload)
        return CashbackResponse("Изменение информации о кэшбэке принято к обработке", cashbackId)
    }

    @GetMapping("getAll/{shopId}")
    fun getShopCashbacks(@PathVariable shopId: Long): List<Cashback> {
        val shop = shopRepository.findById(shopId).orElseThrow {
            EntityNotFoundException("Магазин с id $shopId не найден!")
        }
        return cashbackRepository.findCashbackByShop(shop)
    }
}
