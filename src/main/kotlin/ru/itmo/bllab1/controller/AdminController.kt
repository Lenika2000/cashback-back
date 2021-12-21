package ru.itmo.bllab1.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.model.Cashback
import ru.itmo.bllab1.model.CashbackData
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.ClientRepository
import ru.itmo.bllab1.repo.ShopRepository
import ru.itmo.bllab1.service.CashbackService
import javax.persistence.EntityNotFoundException

data class CashbackRequestPayload(
        val clientId: Long,
        val shopName: String,
        val productName: String,
        val productPrice: Double
)

data class CashbackResponse(
        val message: String,
        val id: Long? = null,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api/admin/cashback")
@RestController
class AdminController(
        private val cashbackService: CashbackService,
        private val clientRepository: ClientRepository,
        private val shopRepository: ShopRepository,
        private val cashbackRepository: CashbackRepository
) {

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun createCashback(@RequestBody payload: CashbackRequestPayload): CashbackResponse {
        val client = clientRepository.findById(payload.clientId).orElseThrow {
            EntityNotFoundException("Клиент с id ${payload.clientId} не найден!")
        }
        val shop = shopRepository.findShopByName(payload.shopName).orElseThrow {
            EntityNotFoundException("Магазин с названием ${payload.shopName} не найден!")
        }

        val cashback = Cashback(0, client = client, shop = shop, productName = payload.productName, productPrice = payload.productPrice, cashbackSum = payload.productPrice * 0.05);
        cashbackRepository.save(cashback)
        return CashbackResponse("Заявка на получение кэшбека для клиента " +
                "${client.firstName} ${client.lastName} от магазина ${shop.name} была создана.", cashback.id)
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun getCashback(): Iterable<CashbackData> {
        return cashbackRepository.findAll().filter { cashback: Cashback -> cashback.shop.name != "" && cashback.client.firstName != ""  }.map{cashback: Cashback -> CashbackController.mapCashbackData(cashback)};
    }

    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun processCashback(): CashbackResponse {
        cashbackService.processCashback();
        return CashbackResponse("Завершена обработка выплат кэшбека для покупок, совершенных более 60 дней назад")
    }
}
