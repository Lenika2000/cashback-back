package ru.itmo.bllab1.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.model.*
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.service.CashbackService
import ru.itmo.bllab1.service.UserService
import javax.persistence.EntityNotFoundException

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api/cashback/")
@RestController
class CashbackController(
        private val cashbackRepository: CashbackRepository,
        private val userService: UserService,
        private val cashbackService: CashbackService
) {


    companion object {
        fun mapCashbackData(cashback: Cashback):  CashbackData =
            CashbackData(cashback.id, cashback.creationDate, cashback.client.firstName, cashback.client.lastName, cashback.shop.name,
                    cashback.productName, cashback.productPrice, cashback.isPaid, cashback.isOrderCompleted, cashback.confirmPayment, cashback.status, cashback.cashbackSum, cashback.shopPayment)
    }

    @GetMapping("{cashbackId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','SHOP')")
    fun getCashback(@PathVariable cashbackId: Long): CashbackData {
        userService.checkShopOrCustomerAuthority(cashbackId);
        val cashback = cashbackRepository.findById(cashbackId).orElseThrow {
            EntityNotFoundException("Кэшбек с id $cashbackId не найден!")
        }
        return mapCashbackData(cashback);
    }

    @PutMapping("{cashbackId}")
    @PreAuthorize("hasAnyRole('ADMIN','SHOP')")
    fun updateCashback(@PathVariable cashbackId: Long, @RequestBody payload: CashbackChangeRequestPayload): CashbackResponse {
        userService.checkShopOrCustomerAuthority(cashbackId)
        cashbackService.updateCashback(payload)
        return CashbackResponse("Изменение информации о кэшбэке принято к обработке")
    }
}
