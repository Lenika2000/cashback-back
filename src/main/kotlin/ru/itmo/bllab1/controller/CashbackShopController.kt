package ru.itmo.bllab1.controller

import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.CashbackStatus
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
        private val cashbackRepository: CashbackRepository,
) {

    @PostMapping("update")
    fun updateCashback(@RequestBody payload: CashbackChangeRequestPayload): CashbackResponse {
        val cashback = cashbackRepository.findById(payload.id).orElseThrow {
            EntityNotFoundException("Кэшбек с id ${payload.id} не найден!")
        }
        payload.isPaid?.let { cashback.isPaid = it }
        payload.isOrderCompleted?.let { cashback.isOrderCompleted = it }
        payload.payment?.let {
            cashback.confirmPayment = true
            cashback.cashbackSum = it
        }
        if (cashback.status == CashbackStatus.NEW)
            cashback.status = CashbackStatus.RECEIVED_INF
        cashbackRepository.save(cashback)
        return CashbackResponse("Изменение информации о кэшбэке принято к обработке", cashback.id)
    }
}
