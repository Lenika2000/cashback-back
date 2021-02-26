package ru.itmo.bllab1.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.itmo.bllab1.controller.CashbackChangeRequestPayload
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.CashbackStatus
import ru.itmo.bllab1.repo.ClientRepository
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException

@Service
class CashbackService(
        private val cashbackRepository: CashbackRepository,
        private val messageService: MessageService,
        private val clientRepository: ClientRepository,
) {
    @Scheduled(cron = "0 0 15 * * ?")
    fun processCashbacks() {
        cashbackRepository.findByStartDateLessThan(LocalDateTime.now().minusDays(60)).forEach { cashback ->
            if (cashback.status != CashbackStatus.NEW && cashback.isPaid && cashback.isOrderCompleted && cashback.confirmPayment) {
                cashback.status = CashbackStatus.APPROVED
                cashback.client.balance += cashback.cashbackSum * 0.9
                clientRepository.save(cashback.client)
                messageService.sendNotificationToClient("Кэшбек зачислен", cashback.client )
            } else {
                cashback.status = CashbackStatus.REJECTED
                messageService.sendNotificationToClient("Кэшбек отклонен", cashback.client )
            }
            cashbackRepository.save(cashback)
        }
    }

    fun updateCashback(payload: CashbackChangeRequestPayload): Long {
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
        return cashback.id
    }
}
