package ru.itmo.bllab1.controller

import ru.itmo.bllab1.repo.Cashback
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.CashbackStatus
import ru.itmo.bllab1.repo.ClientRepository
import ru.itmo.bllab1.repo.ShopRepository
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException

data class CashbackRequestPayload(
        val clientId: Long,
        val shopName: String,
)

data class CashbackResponse(
        val message: String,
        val id: Long? = null,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api/client/cashback/")
@RestController
class CashbackClientController(
        private val cashbackRepository: CashbackRepository,
        private val clientRepository: ClientRepository,
        private val shopRepository: ShopRepository,
) {

    @PostMapping("create")
    fun createCashback(@RequestBody payload: CashbackRequestPayload): CashbackResponse {
        val client = clientRepository.findById(payload.clientId).orElseThrow {
            EntityNotFoundException("Клиент с id ${payload.clientId} не найден!")
        }
        val shop = shopRepository.findShopByName(payload.shopName).orElseThrow {
            EntityNotFoundException("Рекламодатель с названием ${payload.shopName} не найден!")
        }
        val cashback = Cashback (0, client = client, shop = shop);
        cashbackRepository.save(cashback)
        return CashbackResponse("Ваша заявка на получение кэшбека была создана. Ожидайте", cashback.id)
    }
}
