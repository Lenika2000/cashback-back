package ru.itmo.bllab1.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.model.Cashback
import ru.itmo.bllab1.repo.AdminRepository
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.ClientRepository
import ru.itmo.bllab1.repo.ShopRepository
import ru.itmo.bllab1.service.CashbackService
import ru.itmo.bllab1.service.UserService
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
@RequestMapping("/api/admin/cashback/")
@RestController
class CashbackAdminController(
        private val cashbackRepository: CashbackRepository,
        private val clientRepository: ClientRepository,
        private val shopRepository: ShopRepository,
        private val cashbackService: CashbackService,
) {

    @PostMapping("create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun createCashback(@RequestBody payload: CashbackRequestPayload): CashbackResponse {
        val client = clientRepository.findById(payload.clientId).orElseThrow {
            EntityNotFoundException("Клиент с id ${payload.clientId} не найден!")
        }
        val shop = shopRepository.findShopByName(payload.shopName).orElseThrow {
            EntityNotFoundException("Магазин с названием ${payload.shopName} не найден!")
        }

        val cashback = Cashback(0, client = client, shop = shop);
        cashbackRepository.save(cashback)
        return CashbackResponse("Заявка на получение кэшбека для клиента " +
                "${client.firstName} ${client.lastName} от магазина ${shop.name} была создана.", cashback.id)
    }

    @PostMapping("process")
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun processCashback(): CashbackResponse {
        cashbackService.processCashbacks();
        return CashbackResponse("Завершена обработка выплат кэшбека для покупок, совершенных более 60 дней назад")
    }
}
