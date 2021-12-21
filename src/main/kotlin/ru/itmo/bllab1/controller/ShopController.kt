package ru.itmo.bllab1.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.model.Cashback
import ru.itmo.bllab1.model.CashbackDataForShop
import ru.itmo.bllab1.repo.*
import ru.itmo.bllab1.service.UserService
import javax.persistence.EntityNotFoundException

data class CashbackChangeRequestPayload(
        val cashbackId: Long,
        val isPaid: Boolean?,
        val isOrderCompleted: Boolean?,
        val payment: Double?,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api/shops/")
@RestController
class ShopController(
        private val userService: UserService,
        private val shopRepository: ShopRepository,
        private val cashbackRepository: CashbackRepository,
) {

    companion object {
        fun mapCashbackDataForShop (cashback: Cashback): CashbackDataForShop =
                CashbackDataForShop(cashback.id, cashback.creationDate, cashback.client.firstName, cashback.client.lastName,
                        cashback.productName, cashback.productPrice, cashback.isPaid, cashback.isOrderCompleted, cashback.confirmPayment, cashback.status, cashback.cashbackSum, cashback.shopPayment)
    }

    @GetMapping("{shopId}/cashback")
    @PreAuthorize("hasAnyRole('ADMIN','SHOP')")
    fun getShopCashback(@PathVariable shopId: Long): Iterable<CashbackDataForShop> {
        userService.checkShopAuthority(shopId)
        val shop = shopRepository.findById(shopId).orElseThrow {
            EntityNotFoundException("Магазин с id $shopId не найден!")
        }
        return cashbackRepository.findCashbackByShop(shop)
                .map { cashback: Cashback -> mapCashbackDataForShop(cashback) };
    }

}
